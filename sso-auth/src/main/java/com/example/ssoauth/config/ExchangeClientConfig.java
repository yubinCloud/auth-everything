package com.example.ssoauth.config;

import com.example.ssoauth.exchange.JupyterExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class ExchangeClientConfig {

    @Autowired
    private ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    @Bean
    @LoadBalanced
    JupyterExchange jupyterExchange() {
        WebClient client = WebClient.builder()
                .filter(reactorLoadBalancerExchangeFilterFunction)
                .baseUrl("lb://jupyter-service" + "/jupyter")
                .build();
        var factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).blockTimeout(Duration.ofSeconds(60)).build();
        return factory.createClient(JupyterExchange.class);
    }
}
