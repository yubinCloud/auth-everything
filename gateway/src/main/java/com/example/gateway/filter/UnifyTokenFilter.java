package com.example.gateway.filter;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * 统一将请求的 token 放到接下来的 header authz 中
 */
@Configuration
@Order(-200)
@Slf4j
public class UnifyTokenFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        // 如果 header 里面已经有 Authz，则直接放行
        if (request.getHeaders().getFirst("Authz") != null) {
            return chain.filter(exchange);
        }
        var mutate = request.mutate();
        String token = null;
        // 尝试从 Query Params 里面获取 token
        token = request.getQueryParams().getFirst("token");
        if (token == null) {
            // 尝试从 Cookie 中获取 token
            var authzCookie = request.getCookies().getFirst("authz");
            if (authzCookie != null) {
                token = authzCookie.getValue();
            }
            if (token == null) {
                String referer = request.getHeaders().getFirst("Referer");
                if (referer != null) {
                    URL refererUrl;
                    try {
                        refererUrl = new URL(referer);
                    } catch (MalformedURLException e) {
                        return chain.filter(exchange);
                    }
                    String query = refererUrl.getQuery();
                    if (query != null) {
                        String[] params = query.split("&");
                        for (String param: params) {
                            if (param.startsWith("token=")) {
                                token = param.split("=")[1];
                                break;
                            }
                        }
                    }
                }
            }
        }
        // 如果获取 token 失败，直接进入下一层转发
        if (token == null) {
            return chain.filter(exchange);
        }
        // 如果获取 token 成功，则统一到 header 中
        mutate.header("Authz", token);
        var moRequest = mutate.build();
        return chain.filter(exchange.mutate().request(moRequest).build());
    }
}
