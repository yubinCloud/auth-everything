package com.example.afleader.config;

import com.example.afleader.exchange.AfWorkerExchange;
import com.example.afleader.exchange.DsWorkerExchange;
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
public class ExchangeConfig {

    private final ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    @Bean
    @LoadBalanced
    AfWorkerExchange afWorkerExchange() {
        WebClient client = WebClient.builder()
                .filter(reactorLoadBalancerExchangeFilterFunction)
                .baseUrl("http://af-worker")
                .build();
        var factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(AfWorkerExchange.class);
    }

    @Bean
    @LoadBalanced
    DsWorkerExchange dsWorkerExchange() {
        WebClient client = WebClient.builder()
                .filter(reactorLoadBalancerExchangeFilterFunction)
                .baseUrl("http://ds-worker")
                .build();
        var factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(DsWorkerExchange.class);
    }
}
