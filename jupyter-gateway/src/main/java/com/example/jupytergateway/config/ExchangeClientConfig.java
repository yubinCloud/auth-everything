package com.example.jupytergateway.config;

import com.example.jupytergateway.exchange.AuthExchange;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class ExchangeClientConfig {

    private final ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    static private final String SSO_AUTH_SERVICE = "lb://sso-auth";

    static private final String SSO_AUTH_SERVICE_BASE_URL = "/auth";

    @Bean
    @LoadBalanced
    AuthExchange authExchange() {
        WebClient client = WebClient.builder()
                .filter(reactorLoadBalancerExchangeFilterFunction)
                .baseUrl(SSO_AUTH_SERVICE + SSO_AUTH_SERVICE_BASE_URL)
                .build();
        var factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).blockTimeout(Duration.ofSeconds(60)).build();
        return factory.createClient(AuthExchange.class);
    }
}
