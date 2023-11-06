package auth

import (
	"af-askari/internal/consts"
	"af-askari/internal/service"
	"context"
	"crypto/rand"
	"encoding/base64"
	"time"

	"github.com/gogf/gf/v2/os/gcache"
	"github.com/golang-jwt/jwt/v5"
)

type sAuth struct{}

func init() {
	service.RegisterAuth(New())
}

func New() service.IAuth {
	return &sAuth{}
}

// 生成 JWT token
func (s *sAuth) CreateJwtToken(username string) (token string, err error) {
	key := []byte("sdacvasdfasdasavbfr")
	claim := jwt.MapClaims{
		"iss": "af-askari",
		"sub": username,
	}
	t := jwt.NewWithClaims(jwt.SigningMethodHS256, claim)
	return t.SignedString(key)
}

// 创建密钥 secret-key
func (s *sAuth) CreateSecretKey(ctx context.Context, username string) (secretKey string, err error) {
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
