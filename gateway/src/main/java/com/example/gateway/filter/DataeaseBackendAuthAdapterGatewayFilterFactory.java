package com.example.gateway.filter;

import com.example.gateway.config.DataeaseConfig;
import com.example.gateway.service.DataeaseService;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 将平台内部用户转换为 dataease 用户
 */
@Component
public class DataeaseBackendAuthAdapterGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Resource
    private DataeaseConfig dataeaseConfig;

    @Resource
    private DataeaseService dataeaseService;

    @Resource
    private Cache<String, String> dataeaseTokenCache;

    @Override
    public GatewayFilter apply(Object config) {
        return new DataeaseBackendAuthAdapterFilter(dataeaseConfig, dataeaseService, dataeaseTokenCache);
    }

    @Slf4j
    static class DataeaseBackendAuthAdapterFilter implements GatewayFilter, Ordered {

        private final DataeaseConfig dataeaseConfig;

        private final DataeaseService dataeaseService;

        private final Cache<String, String> tokenCache;

        static private final String AUTHORIZATION_HEADER = "Authorization";

        public DataeaseBackendAuthAdapterFilter(DataeaseConfig dataeaseConfig, DataeaseService dataeaseService, Cache<String, String> tokenCache) {
            this.dataeaseConfig = dataeaseConfig;
            this.dataeaseService = dataeaseService;
            this.tokenCache = tokenCache;
        }

        @Override
        public int getOrder() {
            return 100;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            if (!dataeaseConfig.isEnableSubsystem()) {
                return chain.filter(exchange);
            }
            var request = exchange.getRequest();
            String username = request.getHeaders().getFirst("User");
            // 先检查本地缓存，miss 的话再远程调用获取
            String cachedToken = tokenCache.getIfPresent(username);
            if (cachedToken != null) {
                exchange.getRequest().mutate().header(AUTHORIZATION_HEADER, cachedToken).build();
                return chain.filter(exchange);
            }
            Mono<String> tokenMono = dataeaseService.fetchAuthToken(username);
            return tokenMono.flatMap(token -> {
                tokenCache.put(username, token);
                exchange.getRequest().mutate().header(AUTHORIZATION_HEADER, token).build();
                return chain.filter(exchange);
            });
        }
    }
}
