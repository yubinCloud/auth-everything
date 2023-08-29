package com.example.jupytergateway.filter;

import com.example.jupytergateway.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

/**
 * 为 WebSocket 连接替换 token
 */
@Component
@Slf4j
public class WebsocketTokenReplaceFilter implements GlobalFilter, Ordered {

    @Autowired
    private UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var request = exchange.getRequest();
        String path = request.getPath().toString();
        var requestUrl = request.getURI();
        if (!path.startsWith("/ws")) {
            return chain.filter(exchange);
        }
        String authzToken = request.getQueryParams().getFirst("token");
        String jupyterToken = userService.findJupyterContextByToken(authzToken);
        String query = "token=" + jupyterToken;
        URI newUri = UriComponentsBuilder.fromUri(requestUrl).replaceQuery(query).build(true).toUri();
        var modRequest = exchange.getRequest().mutate().uri(newUri).build();
        return chain.filter(exchange.mutate().request(modRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
