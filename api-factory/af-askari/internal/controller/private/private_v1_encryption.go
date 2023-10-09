package private

import (
	"af-askari/api/private/v1"
	"af-askari/internal/service/encryption_service"
	"context"
)

func (c *ControllerV1) FetchSecretKey(ctx context.Context, req *v1.FetchSecretKeyReq) (res *v1.FetchSecretKeyRes, err error) {
	secretKey, err := encryption_service.CreateSecretKey(ctx, req.Username)
	res = &v1.FetchSecretKeyRes{
		SecretKey: secretKey,
		Alg:       "AES",
	}
	return
}
