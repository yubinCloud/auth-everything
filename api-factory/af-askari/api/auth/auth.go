// =================================================================================
// Code generated and maintained by GoFrame CLI tool. DO NOT EDIT. 
// =================================================================================

package auth

import (
	"context"
	
	"af-askari/api/auth/v1"
)

type IAuthV1 interface {
	AuthLogin(ctx context.Context, req *v1.AuthLoginReq) (res *v1.AuthLoginRes, err error)
}


