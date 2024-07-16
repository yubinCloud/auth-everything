package com.example.eusergateway.filter;

import com.example.eusergateway.config.DataeaseConfig;
import com.example.eusergateway.constant.RequestHeaderConstant;
import com.example.eusergateway.service.DataeaseService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * 替换发向 dataease backend 的 request 中的 token
 */
@Component
public class DataeaseBackendAuthAdapterGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    @Resource
    private DataeaseConfig dataeaseConfig;

    @Resource
    private DataeaseService dataeaseService;

    @Override
    public GatewayFilter apply(Object config) {
        return new DataeaseBackendAuthAdapterFilter(dataeaseConfig, dataeaseService);
    }

    @Slf4j
    static class DataeaseBackendAuthAdapterFilter implements GatewayFilter, Ordered {

        private final DataeaseConfig dataeaseConfig;

        private final DataeaseService dataeaseService;

        private final Cache<String, String> tokenCache;

        static private final String AUTHORIZATION_HEADER = "Authorization";

        public DataeaseBackendAuthAdapterFilter(DataeaseConfig dataeaseConfig, DataeaseService dataeaseService) {
            this.dataeaseConfig = dataeaseConfig;
            this.dataeaseService = dataeaseService;
            this.tokenCache = Caffeine.newBuilder()
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .initialCapacity(30)
                    .maximumSize(500)
                    .build();
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
            String euser = request.getHeaders().getFirst(RequestHeaderConstant.EUSER_HEADER);
            // 先检查本地缓存
            String cachedToken = tokenCache.getIfPresent("admin");  // TODO：现在暂时全部换为 admin 的 token，之后需要解决这里
            if (cachedToken != null) {
                replaceRequestToken(exchange, cachedToken);
                return chain.filter(exchange);
            }
            Mono<String> tokenMono = dataeaseService.fetchAuthToken("admin");
            return tokenMono.flatMap(token -> {
                tokenCache.put("admin", token);
                replaceRequestToken(exchange, token);
                return chain.filter(exchange);
            });
        }

        private void replaceRequestToken(ServerWebExchange exchange, String dataeaseToken) {
            exchange.getRequest().mutate().header(AUTHORIZATION_HEADER, dataeaseToken).build();
        }
    }
}
