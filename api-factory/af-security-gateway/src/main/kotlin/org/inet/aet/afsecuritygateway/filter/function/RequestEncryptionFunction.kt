package org.inet.aet.afsecuritygateway.filter.function



import org.inet.aet.afsecuritygateway.service.EncryptService
import org.inet.aet.afsecuritygateway.util.parseAfRoutePath
import org.reactivestreams.Publisher
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

class RequestEncryptionFunction(private val encryptService: EncryptService): RewriteFunction<String, String> {

    override fun apply(exchange: ServerWebExchange, body: String?): Publisher<String> {
        val routePath = parseAfRoutePath(exchange.request.path.value())
        // 获取解密所需信息
        val routeLocal = encryptService.findRouteLocal(routePath)
        // 解密
        val requestBody = encryptService.decrypt(body, routeLocal)
        return Mono.just(requestBody)
    }

}