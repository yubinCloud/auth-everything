package v1

import "github.com/gogf/gf/v2/frame/g"

type AuthLoginReq struct {
	g.Meta   `path:"/auth/login" tags:"Auth" method:"post" summary:"用户登录"`
	Username string `json:"username"`
	Password string `json:"password"`
}
type AuthLoginRes struct {
	SecretKey string `json:"secretKey" dc:"使用 Base64 标准方式对字节数组进行了编码，使用时需要先将其按照 Base64 进行解码得到 128 位的字节数组"`
	Token     string `json:"token" dc:"JWT token，之后请携带该 token 请求加密 API，该 token 置于 Authorization 字段中"`
}
