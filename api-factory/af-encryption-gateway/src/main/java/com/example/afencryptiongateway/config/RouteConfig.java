package com.example.afencryptiongateway.config;

import com.example.afencryptiongateway.exchange.AskariExchange;
import com.example.afencryptiongateway.function.RequestEncryptionFunction;
import com.example.afencryptiongateway.function.ResponseEncryptionFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置网关路由
 */
@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    private final AskariExchange askariExchange;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder, ObjectMapper objectMapper) {
        return builder
                .routes()
                .route("public-api", r -> r.path("/public-api/**")
                        .filters(f -> f
                                .stripPrefix(1)
//                                .modifyRequestBody(String.class, String.class, new RequestEncryptionFunction(objectMapper, askariExchange))
                                .modifyResponseBody(String.class, String.class, new ResponseEncryptionFunction(objectMapper, askariExchange)))
                        .uri("lb://dynamic-api")
                ).build();
    }
}
