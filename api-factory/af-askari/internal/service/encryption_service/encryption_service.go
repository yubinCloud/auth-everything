package encryption_service

import (
	"af-askari/internal/consts"
	"context"
	"crypto/rand"
	"encoding/base64"
	"github.com/gogf/gf/v2/os/gcache"
	"time"
)

func CreateSecretKey(ctx context.Context, username string) (secretKey string, err error) {
	cacheKey := consts.SecretKeyCachePrefix + username
	cacheValue := gcache.MustGet(ctx, cacheKey)
	if cacheValue.Val() == nil {
		keyBytes := make([]byte, 16)
		_, err := rand.Read(keyBytes)
		if err != nil {
			return "", err
		}
		secretKey = base64.StdEncoding.EncodeToString(keyBytes)
		err = gcache.Set(ctx, cacheKey, secretKey, 24*30*time.Hour)
		if err != nil {
			return "", err
		}
	} else {
		secretKey = cacheValue.String()
	}
	return secretKey, nil
}
