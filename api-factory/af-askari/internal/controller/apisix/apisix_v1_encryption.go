package apisix

import (
	"af-askari/internal/consts"
	"context"
	"crypto/rand"
	"encoding/base64"
	"github.com/gogf/gf/v2/os/gcache"
	"time"

	"af-askari/api/apisix/v1"
)

func (c *ControllerV1) FetchSecretKey(ctx context.Context, req *v1.FetchSecretKeyReq) (res *v1.FetchSecretKeyRes, err error) {
	var secretKey string
	cacheKey := consts.SecretKeyCachePrefix + req.Username
	cacheValue := gcache.MustGet(ctx, cacheKey)
	if cacheValue.Val() == nil {
		keyBytes := make([]byte, 16)
		_, err := rand.Read(keyBytes)
		if err != nil {
			return nil, err
		}
		secretKey = base64.StdEncoding.EncodeToString(keyBytes)
		err = gcache.Set(ctx, cacheKey, secretKey, 24*30*time.Hour)
		if err != nil {
			return nil, err
		}
	} else {
		secretKey = cacheValue.String()
	}
	res = &v1.FetchSecretKeyRes{
		SecretKey: secretKey,
		Alg:       "AES",
	}
	return
}
