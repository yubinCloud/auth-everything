package com.example.afencryptiongateway.util;

import cn.hutool.json.JSONException;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.example.afencryptiongateway.exception.ForbidRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;

public class JwtUtil {
    public static String parseUsername(ServerWebExchange exchange) {
        String token = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        JWT jwt;
        try {
            jwt = JWTUtil.parseToken(token);
        } catch (JSONException e) {
            throw new ForbidRequestException("JWT token 错误");
        }
        Object sub = jwt.getPayload("sub");
        if (sub == null) {
            throw new ForbidRequestException("JWT token 错误");
        }
        return (String) sub;
    }
}
