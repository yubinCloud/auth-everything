// =================================================================================
// Code generated and maintained by GoFrame CLI tool. DO NOT EDIT. 
// =================================================================================

package private

import (
	"context"
	
	"af-askari/api/private/v1"
)

type IPrivateV1 interface {
	FetchSecretKey(ctx context.Context, req *v1.FetchSecretKeyReq) (res *v1.FetchSecretKeyRes, err error)
}


