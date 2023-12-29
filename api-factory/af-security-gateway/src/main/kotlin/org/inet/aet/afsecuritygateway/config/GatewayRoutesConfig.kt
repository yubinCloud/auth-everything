package org.inet.aet.afsecuritygateway.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.inet.aet.afsecuritygateway.filter.AfRoutePermissionCheckGatewayFilter
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayRoutesConfig(
    val afRoutePermissionCheckGatewayFilter: AfRoutePermissionCheckGatewayFilter,
) {

    @Bean
    fun routes(builder: RouteLocatorBuilder, objectMapper: ObjectMapper): RouteLocator {
        return builder.routes()
            // ******************* af-worker 服务 ************************
            .route("public-api") { r: PredicateSpec -> r.path("/public-api/**").filters { f -> afWorkerFilterFunc(f) }.uri("lb://af-worker") }
            .build()
    }

    private fun afWorkerFilterFunc(spec: GatewayFilterSpec): GatewayFilterSpec {
        return spec.rewritePath("/public-api/?(?<segment>.*)", "/dynamic/\${segment}").filter(afRoutePermissionCheckGatewayFilter)
    }

}
