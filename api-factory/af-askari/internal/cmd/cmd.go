package cmd

import (
	"af-askari/internal/controller/apisix"
	"context"

	"github.com/gogf/gf/v2/frame/g"
	"github.com/gogf/gf/v2/net/ghttp"
	"github.com/gogf/gf/v2/os/gcmd"

	"af-askari/internal/controller/hello"
)

var (
	Main = gcmd.Command{
		Name:  "main",
		Usage: "main",
		Brief: "start http server",
		Func: func(ctx context.Context, parser *gcmd.Parser) (err error) {
			s := g.Server()
			s.Group("/", func(group *ghttp.RouterGroup) {
				group.Middleware(ghttp.MiddlewareHandlerResponse)
				group.Bind(
					hello.NewV1(),
					apisix.NewV1(),
				)
			})
			s.BindHandler("GET:/health/live", func(r *ghttp.Request) {
				r.Response.WriteJson(g.Map{
					"status": "UP",
				})
			})
			s.SetPort(10510)
			s.Run()
			return nil
		},
	}
)
