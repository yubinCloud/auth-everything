package org.inet.aet.chatanalysis.config

import org.inet.aet.chatanalysis.exchange.api.DsWorkerExchange
import org.springframework.cloud.client.loadbalancer.LoadBalanced
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.support.WebClientAdapter
import org.springframework.web.service.invoker.HttpServiceProxyFactory

@Configuration
class HttpExchangeConfig (
    val reactorLoadBalancerExchangeFilterFunction: ReactorLoadBalancerExchangeFilterFunction
) {

    @Bean
    @LoadBalanced
    fun dsWorkerExchange(): DsWorkerExchange {
        val client = WebClient.builder()
            .filter(reactorLoadBalancerExchangeFilterFunction)
            .baseUrl("lb://ds-worker")
            .build()
        val factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(client)).build()
        return factory.createClient(DsWorkerExchange::class.java)
    }

}