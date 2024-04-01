package main

import (
	"bytes"
	"context"
	"errors"
	"fmt"
	"github.com/nacos-group/nacos-sdk-go/v2/clients"
	"github.com/nacos-group/nacos-sdk-go/v2/clients/naming_client"
	"github.com/nacos-group/nacos-sdk-go/v2/common/constant"
	"github.com/nacos-group/nacos-sdk-go/v2/model"
	"github.com/nacos-group/nacos-sdk-go/v2/vo"
	log "github.com/sirupsen/logrus"
	"go.uber.org/atomic"
	"io"
	"net/http"
	"net/url"
	"os"
	"os/signal"
	"strconv"
	"strings"
	"syscall"
	"time"
)

var (
	RequestPathError         = errors.New("request path error")
	TargetURLParseError      = errors.New("target-url parse error")
	TargetRequestCreateError = errors.New("target-request create error")
	ProxyServicePortError    = errors.New("service port parse error")
)

var appPort int
var namingClient naming_client.INamingClient // nacos 客户端
var healthCheckURL string
var proxyServiceName string
var proxyServiceIP string
var proxyServicePort int

func InitAppConfig() {
	var err error
	appPortString := os.Getenv("APP_PORT")
	if appPortString == "" {
		appPort = 8080
	} else {
		appPort, err = strconv.Atoi(appPortString)
		if err != nil {
			panic(errors.New("app port parse error"))
		}
	}

	healthCheckURL = os.Getenv("SERVICE_HEALTH_CHECK_URL")

	proxyServiceName = os.Getenv("APP_NAME")
	if proxyServiceName == "" {
		panic(errors.New("app name must not blank"))
	}

	proxyServiceIP = os.Getenv("SERVICE_IP")
	if proxyServiceIP == "" {
		panic(errors.New("service ip must not blank"))
	}

	servicePort := os.Getenv("SERVICE_PORT")
	if servicePort != "" {
		proxyServicePort, err = strconv.Atoi(servicePort)
		if err != nil {
			panic(ProxyServicePortError)
		}
	} else {
		panic(errors.New("service port must not blank"))
	}
}

// 通过服务名从 Nacos 服务中心发现服务实例
// 待改进点：
// 1. 加入 serviceName -> Instance 的缓存，减少对 Nacos 服务中心的请求
// 2. 缓存后，通过订阅 nacos 的方式来更新缓存，从而延长缓存时间
func discoveryService(serviceName string) *model.Instance {
	instance, err := namingClient.SelectOneHealthyInstance(vo.SelectOneHealthInstanceParam{
		ServiceName: serviceName,
	})
	if err != nil {
		log.Fatal(err)
		return nil
	}
	return instance
}

// 解析原始 URL，获取最终需要转发的 URL
func parseTargetURL(rawURL *url.URL) (*url.URL, error) {
	path := rawURL.Path
	pathSegments := strings.Split(path, "/")
	if len(pathSegments) <= 2 {
		log.Fatal("path error")
		return nil, RequestPathError
	}
	serviceName := pathSegments[1]
	realPath := strings.Join(pathSegments[2:], "/")
	instance := discoveryService(serviceName)
	targetURL, err := url.Parse(fmt.Sprintf("http://%s:%d/%s", instance.Ip, instance.Port, realPath))
	if err != nil {
		return nil, TargetURLParseError
	}
	targetURL.RawQuery = rawURL.RawQuery
	return targetURL, nil
}

var ExpectedHealthyRespBody = []byte("{\"status\":\"UP\"}")

func forwardHandler(w http.ResponseWriter, r *http.Request) {
	targetURL, err := parseTargetURL(r.URL)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadGateway)
		return
	}
	if targetURL.Host == "actuator" {
		_, err = w.Write(ExpectedHealthyRespBody)
		if err != nil {
			http.Error(w, err.Error(), http.StatusInternalServerError)
		}
		return
	}
	// 创建新请求，并将请求发送到目标服务中
	targetReq, err := http.NewRequest(r.Method, targetURL.String(), r.Body)
	if err != nil {
		http.Error(w, TargetRequestCreateError.Error(), http.StatusBadGateway)
		return
	}
	targetReq.Header = r.Header
	targetResp, err := http.DefaultTransport.RoundTrip(targetReq)
	if err != nil {
		http.Error(w, err.Error(), http.StatusBadGateway)
		return
	}
	defer func() {
		_ = targetResp.Body.Close()
	}()
	// 读取目标服务的响应，并将其写入到原始请求的响应中
	w.WriteHeader(targetResp.StatusCode)
	for headerKey, headerValues := range targetResp.Header {
		for _, value := range headerValues {
			w.Header().Set(headerKey, value)
		}
	}
	_, err = io.Copy(w, targetResp.Body)
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
}

func checkHealthRespBody(resp *http.Response) bool {
	if resp.StatusCode != http.StatusOK {
		return false
	}
	body, _ := io.ReadAll(resp.Body)
	if !bytes.Equal(body, ExpectedHealthyRespBody) {
		return false
	}
	return true
}

func healthCheck() bool {
	resp, err := http.Get(healthCheckURL)
	if err != nil {
		log.Println(err.Error())
		return false
	}
	defer func() {
		_ = resp.Body.Close()
	}()
	if !checkHealthRespBody(resp) {
		return false
	}
	return true
}

var registerProxyParam *vo.RegisterInstanceParam

func InitRegisterProxyParam() {
	registerProxyParam = &vo.RegisterInstanceParam{
		Ip:          proxyServiceIP,
		Port:        uint64(proxyServicePort),
		ServiceName: proxyServiceName,
		Weight:      10,
		Enable:      true,
		Healthy:     true,
		Ephemeral:   true,
	}
}

var needLogRegisterInfo = atomic.NewBool(true)

func registerProxy() {
	_, err := namingClient.RegisterInstance(*registerProxyParam)
	if err != nil {
		log.Fatal(err)
	} else {
		if needLogRegisterInfo.Load() {
			log.Println(fmt.Sprintf("register %s, address: %s:%d", proxyServiceName, proxyServiceIP, proxyServicePort))
			needLogRegisterInfo.Store(false)
		}
	}
}

var deregisterProxyParam *vo.DeregisterInstanceParam

func InitDeregisterProxyParam() {
	deregisterProxyParam = &vo.DeregisterInstanceParam{
		Ip:          proxyServiceIP,
		Port:        uint64(proxyServicePort),
		ServiceName: proxyServiceName,
	}
}

func deregisterProxy() {
	_, err := namingClient.DeregisterInstance(*deregisterProxyParam)
	if err != nil {
		log.Fatal(err)
	} else {
		log.Println(fmt.Sprintf("deregister %s, address: %s:%d", proxyServiceName, proxyServiceIP, proxyServicePort))
		needLogRegisterInfo.Store(true)
	}
}

func healthCheckLoop() {
	for {
		isHealthy := true
		if healthCheckURL != "" {
			isHealthy = healthCheck()
		}
		if isHealthy {
			registerProxy()
		} else {
			deregisterProxy()
		}
		time.Sleep(12 * time.Second)
	}
}

var nacosHost string
var nacosPort int

func InitNacosClient() error {
	// 从 env 获取 nacos 配置
	var err error
	nacosHost = os.Getenv("NACOS_HOST")
	if nacosHost == "" {
		nacosHost = "127.0.0.1"
	}
	nacosPortStr := os.Getenv("NACOS_PORT")
	if nacosPortStr == "" {
		nacosPort = 8848
	} else {
		nacosPort, err = strconv.Atoi(nacosPortStr)
		if err != nil {
			panic(errors.New("nacos port parse error"))
		}
	}
	// create ServerConfig and ClientConfig
	nacosServerConfig := []constant.ServerConfig{
		*constant.NewServerConfig(nacosHost, uint64(nacosPort), constant.WithContextPath("/nacos")),
	}
	nacosClientConfig := *constant.NewClientConfig(
		constant.WithNamespaceId(""),
		constant.WithTimeoutMs(5000),
		constant.WithNotLoadCacheAtStart(true),
		constant.WithLogDir("/tmp/nacos/log"),
		constant.WithCacheDir("/tmp/nacos/cache"),
		constant.WithLogLevel("debug"),
	)
	// create naming client
	namingClient, err = clients.NewNamingClient(
		vo.NacosClientParam{
			ClientConfig:  &nacosClientConfig,
			ServerConfigs: nacosServerConfig,
		},
	)
	return err
}

// 服务关闭时需要释放资源的逻辑
func shutdownHook() {
	deregisterProxy()
}

// InitLogger 初始化日志配置
func InitLogger() {
	log.SetFormatter(&log.TextFormatter{})
	log.SetOutput(os.Stdout)
	log.SetReportCaller(true)
}

func main() {
	// 初始化相关配置和参数
	InitLogger()
	InitAppConfig()
	InitRegisterProxyParam()
	InitDeregisterProxyParam()
	err := InitNacosClient()
	if err != nil {
		panic(err)
	}
	// 启动 health check 的循环
	go healthCheckLoop()
	// 生成 HTTP server 并启动
	server := &http.Server{
		Addr:    fmt.Sprintf(":%d", appPort),
		Handler: http.HandlerFunc(forwardHandler),
	}
	go func() {
		printBanner()
		if err = server.ListenAndServe(); err != nil && !errors.Is(http.ErrServerClosed, err) {
			panic(err)
		}
	}()
	// 等待 OS 的 signal，从而优雅关闭服务
	stop := make(chan os.Signal, 1)
	signal.Notify(stop, syscall.SIGINT, syscall.SIGTERM)
	<-stop
	log.Println("Server is shutting down...")
	shutdownHook()

	// 关闭服务，并等待已有的连接关闭
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	if err = server.Shutdown(ctx); err != nil {
		log.Fatal(fmt.Sprintf("Error stopping server: %s", err))
	}
	log.Println("Server stopped.")
}

func printBanner() {
	banner := "  _   _          _____ ____   _____     _____ _____ _____  ______ _____          _____  \n | \\ | |   /\\   / ____/ __ \\ / ____|   / ____|_   _|  __ \\|  ____/ ____|   /\\   |  __ \\ \n |  \\| |  /  \\ | |   | |  | | (___    | (___   | | | |  | | |__ | |       /  \\  | |__) |\n | . ` | / /\\ \\| |   | |  | |\\___ \\    \\___ \\  | | | |  | |  __|| |      / /\\ \\ |  _  / \n | |\\  |/ ____ \\ |___| |__| |____) |   ____) |_| |_| |__| | |___| |____ / ____ \\| | \\ \\ \n |_| \\_/_/    \\_\\_____\\____/|_____/   |_____/|_____|_____/|______\\_____/_/    \\_\\_|  \\_\\"
	fmt.Println(banner)
	log.Printf("sidecar <%s> successfully started on port %d", proxyServiceName, appPort)
}
