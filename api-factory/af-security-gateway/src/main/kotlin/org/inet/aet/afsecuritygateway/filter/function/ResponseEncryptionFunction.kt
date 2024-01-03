package org.inet.aet.afsecuritygateway.filter.function

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.benmanes.caffeine.cache.Cache
import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.recipes.cache.CuratorCache
import org.inet.aet.afsecuritygateway.entity.RouteLocal
import org.inet.aet.afsecuritygateway.service.EncryptService
import org.inet.aet.afsecuritygateway.util.parseAfRoutePath
import org.reactivestreams.Publisher
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class ResponseEncryptionFunction(private val encryptService: EncryptService): RewriteFunction<String, String> {

    override fun apply(exchange: ServerWebExchange, body: String?): Publisher<String> {
        val routePath = parseAfRoutePath(exchange.request.path.value())
        // 获取加密所需信息
        val routeLocal = encryptService.findRouteLocal(routePath)
        // 加密
        val responseBody = encryptService.encrypt(body, routeLocal)
        return Mono.just(responseBody)
    }

}