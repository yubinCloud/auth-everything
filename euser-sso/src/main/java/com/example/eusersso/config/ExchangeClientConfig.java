package com.example.eusersso.config;

import com.example.eusersso.exchange.ShanDongTongExchange;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
@RequiredArgsConstructor
public class ExchangeClientConfig {

    private final ShanDongTongProperties sdtProperties;

    @Bean
    ShanDongTongExchange shanDongTongExchange() {
        WebClient client = WebClient.builder()
                .baseUrl(sdtProperties.getEndpointUrl())
                .build();
        var factory = HttpServiceProxyFactory.builder(WebClientAdapter.forClient(client)).build();
        return factory.createClient(ShanDongTongExchange.class);
    }
}
