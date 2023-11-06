package com.example.afencryptiongateway.config;

import com.example.afencryptiongateway.exchange.AskariExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ExchangeClientConfig {

    @Autowired
    private ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    @Bean
    @LoadBalanced
    AskariExchange askariExchange() {
        WebClient client = WebClient.builder()
                .filter(reactorLoadBalancerExchangeFilterFunction)
                .baseUrl("lb://af-askari")
                .build();
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(AskariExchange.class);
    }
}
