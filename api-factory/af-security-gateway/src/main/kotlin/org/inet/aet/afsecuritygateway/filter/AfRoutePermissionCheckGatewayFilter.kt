package org.inet.aet.afsecuritygateway.filter

import org.inet.aet.afsecuritygateway.exception.AbsentPermissionException
import org.inet.aet.afsecuritygateway.repository.AfRoutePermRepository
import org.inet.aet.afsecuritygateway.util.parseAfRoutePath
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class AfRoutePermissionCheckGatewayFilter(private val afRoutePermRepository: AfRoutePermRepository): GatewayFilter, Ordered {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val routePath = parseAfRoutePath(request.path.value())
        println(routePath)
        val username = request.headers.getFirst("X-Euser")
        println("username: $username")
        return afRoutePermRepository.queryPermissions(username)
            .any(routePath::equals)
            .flatMap { valid: Boolean ->  if (valid) chain.filter(exchange) else Mono.error(AbsentPermissionException()) }
    }

    override fun getOrder(): Int {
        return 100
    }

}