package v1

import (
	"github.com/gogf/gf/v2/frame/g"
)

type HelloReq struct {
	g.Meta `path:"/hello" tags:"Hello" method:"get" summary:"You first hello api"`
}
type HelloRes struct {
	g.Meta `mime:"text/html" example:"string"`
}

type HealthCheckReq struct {
	g.Meta `path:"/health" tags:"Hello" method:"get" summary:"health check"`
}
type HealthCheckRes struct {
	Status string `json:"status"`
}
