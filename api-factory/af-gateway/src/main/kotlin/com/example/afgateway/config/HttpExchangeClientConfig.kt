package com.example.afgateway.config

import com.example.afgateway.exchange.EuserSSOExchange
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class HttpExchangeClientConfig(
    val reactorLoadBalancerExchangeFilterFunction: ReactorLoadBalancerExchangeFilterFunction
) {

    @Bean
    @LoadBalanced
    fun euserSSOExchange(): EuserSSOExchange {
        val client = WebClient.builder()
            .filter(reactorLoadBalancerExchangeFilterFunction)
            .baseUrl("http://euser-sso/euser-sso")
            .build()
        val factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build()
        return factory.createClient(EuserSSOExchange::class.java)
    }
}