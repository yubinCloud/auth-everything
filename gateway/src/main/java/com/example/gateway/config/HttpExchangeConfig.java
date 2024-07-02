package com.example.gateway.config;

import com.example.gateway.exchange.DataeaseBackendExchange;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class HttpExchangeConfig {

    private final ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    @Bean
    @LoadBalanced
    DataeaseBackendExchange dataeaseBackendExchange() {
        WebClient client = WebClient.builder()
                .filter(reactorLoadBalancerExchangeFilterFunction)
                .baseUrl("http://dataease-backend/de-api")
                .build();
        var factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(DataeaseBackendExchange.class);
    }

}
