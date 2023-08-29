package com.example.jupytergateway.filter;

import com.example.jupytergateway.service.JupyterService;
import com.example.jupytergateway.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AuthorizationAdaptFilter implements WebFilter {

    private final UserService userService;

    private final JupyterService jupyterService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        String path = request.getPath().toString();
        // 如果是静态资源，直接放行
        if (path.endsWith(".js")
                || path.endsWith(".ico")
                || path.endsWith(".css")
                || path.matches("/jupyter/user/[^/]+/lab/api/settings/@jupyterlab/.*")
                || path.matches("/jupyter/user/[^/]+/lab/extensions/@jupyter-widgets/jupyterlab-manager/.*")
        ) {
            return chain.filter(exchange);
        }
        var mutate = request.mutate();
        // 如果是 WebSocket，替换 token
        if (path.startsWith("/ws")) {
            return chain.filter(exchange);
        }
        // 如果是正常 API 请求，则替换 Header 中的 Authorization
        var username = request.getHeaders().getFirst("User");
        if (username == null || username.equals("")) {
            return chain.filter(exchange);
        }
        String jupyterToken = jupyterService.findToken(username);
        mutate.header("Authorization", "token " + jupyterToken);
        ServerHttpRequest build = mutate.build();
        exchange.mutate().request(build).build();
        return chain.filter(exchange);
    }
}
