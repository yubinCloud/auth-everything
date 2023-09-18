package apisix

import (
	"context"

	"github.com/gogf/gf/v2/errors/gcode"
	"github.com/gogf/gf/v2/errors/gerror"

	"af-askari/api/apisix/v1"
)

func (c *ControllerV1) FetchSecretKey(ctx context.Context, req *v1.FetchSecretKeyReq) (res *v1.FetchSecretKeyRes, err error) {
	return nil, gerror.NewCode(gcode.CodeNotImplemented)
}
