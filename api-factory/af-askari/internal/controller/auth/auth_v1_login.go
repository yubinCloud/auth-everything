package auth

import (
	"af-askari/internal/service/auth_service"
	"af-askari/internal/service/encryption_service"
	"context"

	v1 "af-askari/api/auth/v1"
)

func (c *ControllerV1) AuthLogin(ctx context.Context, req *v1.AuthLoginReq) (res *v1.AuthLoginRes, err error) {
	if req.Username != "admin" || req.Password != "vA2S2pPVzt" {
		res = &v1.AuthLoginRes{
			SecretKey: "",
			Token:     "",
		}
		return
	}
	secretKey, err := encryption_service.CreateSecretKey(ctx, req.Username)
	if err != nil {
		return nil, err
	}
	jwtToken, err := auth_service.CreateJwtToken(req.Username)
	res = &v1.AuthLoginRes{
		SecretKey: secretKey,
		Token:     jwtToken,
	}
	return
}
