package com.example.eusergateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Configuration
@Order(-200)
@Slf4j
public class UnifyTokenFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        // 如果 header 里面已经有 Eauth，则直接放行
        if (Objects.nonNull(request.getHeaders().getFirst("Eauth"))) {
            return chain.filter(exchange);
        }
        String token;
        // 检查 Cookie 中的 eauth
        var eauthCookie = request.getCookies().getFirst("eauth");
        if (Objects.nonNull(eauthCookie)) {
            token = eauthCookie.getValue();
            return addTokenToHeaderAndForward(exchange, chain, request, token);
        }
        // 检查 Header 的 Authorization 字段
        token = request.getHeaders().getFirst("Authorization");
        if (Objects.nonNull(token)) {
            return addTokenToHeaderAndForward(exchange, chain, request, token);
        }
        return chain.filter(exchange);
    }

    private Mono<Void> addTokenToHeaderAndForward(ServerWebExchange exchange, WebFilterChain chain, ServerHttpRequest request, String token) {
        var mutate = request.mutate();
        mutate.header("Eauth", token);
        var moRequest = mutate.build();
        return chain.filter(exchange.mutate().request(moRequest).build());
    }
}
