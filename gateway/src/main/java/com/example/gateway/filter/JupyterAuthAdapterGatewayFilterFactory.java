package com.example.gateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.example.gateway.service.JupyterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class JupyterAuthAdapterGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final JupyterService jupyterService;

    @Override
    public GatewayFilter apply(Object config) {
        return new JupyterAuthAdapterFilter(jupyterService);
    }

    @Slf4j
    static class JupyterAuthAdapterFilter implements GatewayFilter, Ordered {

        private final JupyterService jupyterService;

        public JupyterAuthAdapterFilter(JupyterService jupyterService) {
            this.jupyterService = jupyterService;
        }

        @Override
        public int getOrder() {
            return 100;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            var request = exchange.getRequest();
            String path = request.getPath().value();
            // 如果是静态资源，则直接放行
            if (path.matches(".*\\.(js|ico|css)")
                    || path.matches("/jupyter/user/[^/]+/lab/api/settings/@jupyterlab/.*")
                    || path.matches("/jupyter/user/[^/]+/lab/extensions/@jupyter-widgets/jupyterlab-manager/.*")
            ) {
                return chain.filter(exchange);
            }
            // 如果是 ws 请求，则更换 query 中的 token
            if (path.startsWith("/ws")) {
                String username = request.getHeaders().getFirst("User");
                if (username == null) {
                    return chain.filter(exchange);
                }
                String jupyterToken = jupyterService.findToken(username);
                String query = "token=" + jupyterToken;
                String[] originalParts = org.springframework.util.StringUtils.tokenizeToStringArray(path, "/");
                StringBuilder newPath = new StringBuilder("/");
                for(int i = 1; i < originalParts.length; ++i) {
                    if (newPath.length() > 1) {
                        newPath.append('/');
                    }
                    newPath.append(originalParts[i]);
                }
                if (newPath.length() > 1 && path.endsWith("/")) {
                    newPath.append('/');
                }
                URI newUri = UriComponentsBuilder.fromUri(request.getURI()).replaceQuery(query).replacePath(newPath.toString()).build(true).toUri();
                request = request.mutate().uri(newUri).build();
                return chain.filter(exchange.mutate().request(request).build());
            }
            // 如果是正常的 API 请求，则替换 Header 中的 Authorization
            String username = request.getHeaders().getFirst("User");
            if (StringUtils.isEmpty(username)) {
                return chain.filter(exchange);
            }
            String jupyterToken = jupyterService.findToken(username);
            request = request.mutate().header("Authorization", "token " + jupyterToken).build();
            return chain.filter(exchange.mutate().request(request).build());
        }
    }
}
