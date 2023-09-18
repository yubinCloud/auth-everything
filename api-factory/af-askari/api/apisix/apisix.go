// =================================================================================
// Code generated and maintained by GoFrame CLI tool. DO NOT EDIT. 
// =================================================================================

package apisix

import (
	"context"
	
	"af-askari/api/apisix/v1"
)

type IApisixV1 interface {
	FetchSecretKey(ctx context.Context, req *v1.FetchSecretKeyReq) (res *v1.FetchSecretKeyRes, err error)
}


