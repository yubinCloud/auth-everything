package com.example.afgateway.exchange

import com.example.afgateway.exchange.response.QueryPublicAPIPermissionResponse
import com.example.afgateway.exchange.response.R
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono

@HttpExchange(url = "/euser-sso/internal")
interface EuserSSOExchange {

    @GetExchange("/perms/public-api")
    fun queryPublicAPIPerms(@RequestParam("un") username: String): Mono<R<QueryPublicAPIPermissionResponse>>

}