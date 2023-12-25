package com.example.afgateway.function

import com.fasterxml.jackson.databind.ObjectMapper
import lombok.RequiredArgsConstructor
import lombok.extern.slf4j.Slf4j
import org.reactivestreams.Publisher
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.web.server.ServerWebExchange

@Slf4j
@RequiredArgsConstructor
class ResponseEncryptionFunction : RewriteFunction<String?, String> {
    private val objectMapper: ObjectMapper? = null

    override fun apply(exchange: ServerWebExchange?, u: String?): Publisher<String>? {
        return null
    }
}
