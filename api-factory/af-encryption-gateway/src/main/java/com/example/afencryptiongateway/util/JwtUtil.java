package com.example.afencryptiongateway.util;

import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

public class JwtUtil {
    public static String parseUsername(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        final JWT jwt = JWTUtil.parseToken(token);
        Object sub = jwt.getPayload("sub");
        if (sub == null) {
            return null;
        }
        return (String) sub;
    }
}
