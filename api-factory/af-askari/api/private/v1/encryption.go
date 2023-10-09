package v1

import "github.com/gogf/gf/v2/frame/g"

type FetchSecretKeyReq struct {
	g.Meta   `path:"/private/secret-key" tags:"Encryption" method:"post" summary:"获取 secret key"`
	Username string `json:"username"`
}
type FetchSecretKeyRes struct {
	SecretKey string `json:"secretKey" dc:"使用 Base64 标准方式对字节数组进行了编码，使用时需要先将其按照 Base64 进行解码得到 128 位的字节数组"`
	Alg       string `json:"alg"`
}
