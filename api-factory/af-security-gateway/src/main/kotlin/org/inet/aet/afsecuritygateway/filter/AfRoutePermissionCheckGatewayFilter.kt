package org.inet.aet.afsecuritygateway.filter

import org.inet.aet.afsecuritygateway.exception.AbsentPermissionException
import org.inet.aet.afsecuritygateway.repository.AfRoutePermRepository
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.core.Ordered
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.StringJoiner

@Component
class AfRoutePermissionCheckGatewayFilter(private val afRoutePermRepository: AfRoutePermRepository): GatewayFilter, Ordered {
    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        println(request.path.value())
        val requestPathSplits = request.path.value().split("/")
        val routePathJoiner = StringJoiner("/", "/", "")
        for (i in 2..<requestPathSplits.size) {
            routePathJoiner.add(requestPathSplits[i])
        }
        val routePath = routePathJoiner.toString()
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