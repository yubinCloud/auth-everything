package com.example.eusersso.config;

import com.example.eusersso.exchange.AvueHelperExchange;
import com.example.eusersso.exchange.ShanDongTongExchange;
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
public class ExchangeClientConfig {

    private final ShanDongTongProperties sdtProperties;

    private final ReactorLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    /**
     * 与山东通交互的 Exchange
     * @return
     */
    @Bean
    ShanDongTongExchange shanDongTongExchange() {
        WebClient client = WebClient.builder()
                .baseUrl(sdtProperties.getEndpointUrl())
                .build();
        var factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(ShanDongTongExchange.class);
    }

    /**
     * 与 avue-helper 交互的 Exchange
     * @return
     */
    @Bean
    AvueHelperExchange avueHelperExchange() {
        WebClient client = WebClient.builder()
                .filter(reactorLoadBalancerExchangeFilterFunction)
                .baseUrl("http://avue-helper/avue-helper")
                .build();
        var factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build();
        return factory.createClient(AvueHelperExchange.class);
    }
}
