package auth_service

import "github.com/golang-jwt/jwt/v5"

func CreateJwtToken(username string) (token string, err error) {
	key := []byte("sdacvasdfasdasavbfr")
	claim := jwt.MapClaims{
		"iss": "af-askari",
		"sub": username,
	}
	t := jwt.NewWithClaims(jwt.SigningMethodHS256, claim)
	return t.SignedString(key)
}
