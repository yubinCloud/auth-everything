package v1

import "github.com/gogf/gf/v2/frame/g"

type FetchSecretKeyReq struct {
	g.Meta   `path:"/encryption/secret-key" tag:"Encryption" method:"post" summary:"获取 secret key"`
	Username string `json:"username"`
}
type FetchSecretKeyRes struct {
	SecretKey string `json:"secretKey"`
	Alg       string `json:"alg"`
}
