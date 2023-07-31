package com.example.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * 用来给每个 Request 的 Header 增加一行 User: xxx，方便后面的服务获得 username
 */
@Configuration
public class UsernameHeaderFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        var token = request.getHeaders().getFirst("Authorization");
        if (token == null) {
            var tokenCookie = request.getCookies().getFirst("authorization");
            if (tokenCookie != null) {
                token = tokenCookie.getValue();
            }
        }
        if (token == null || token.equals("")) {
            return chain.filter(exchange);  // 如果不存在 token，直接放行
        }
        String loginId = (String) StpUtil.getLoginIdByToken(token);
        if (loginId == null) {
            return chain.filter(exchange);  // 即便无法从 token 中解析出 loginId 也直接放行
        }
        var mutate = request.mutate();
        mutate.header("User", loginId);
        ServerHttpRequest build = mutate.build();
        exchange.mutate().request(build).build();
        return chain.filter(exchange);
    }
}
