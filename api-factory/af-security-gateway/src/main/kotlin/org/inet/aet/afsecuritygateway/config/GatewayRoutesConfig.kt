package org.inet.aet.afsecuritygateway.config

import org.inet.aet.afsecuritygateway.filter.AfRoutePermissionCheckGatewayFilter
import org.inet.aet.afsecuritygateway.filter.function.RequestEncryptionFunction
import org.inet.aet.afsecuritygateway.filter.function.ResponseEncryptionFunction
import org.inet.aet.afsecuritygateway.service.EncryptService
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec
import org.springframework.cloud.gateway.route.builder.PredicateSpec
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GatewayRoutesConfig(
    val afRoutePermissionCheckGatewayFilter: AfRoutePermissionCheckGatewayFilter,
    val encryptService: EncryptService,
) {

    @Bean
    fun routes(builder: RouteLocatorBuilder): RouteLocator {
        return builder.routes()
            // ******************* af-worker 服务 ************************
            .route("public-api") { r: PredicateSpec -> r.path("/public-api/**").filters { f -> afWorkerFilterFunc(f) }.uri("lb://af-worker") }
            .build()
    }

    private fun afWorkerFilterFunc(spec: GatewayFilterSpec): GatewayFilterSpec {
        return spec
            .rewritePath("/public-api/?(?<segment>.*)", "/dynamic/\${segment}")  // rewrite path to af-worker prefix `/dynamic`
            .filter(afRoutePermissionCheckGatewayFilter)   // check route permission
            .modifyRequestBody(String::class.java, String::class.java, RequestEncryptionFunction(encryptService))   // 对 request body 解密
            .modifyResponseBody(String::class.java, String::class.java, ResponseEncryptionFunction(encryptService)) // 对 response body 加密
    }
}
