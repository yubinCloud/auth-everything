package org.inet.aet.afsecuritygateway.exchange

import org.inet.aet.afsecuritygateway.exchange.response.QueryPublicAPIPermissionResponse
import org.inet.aet.afsecuritygateway.exchange.response.EuserSSOResp
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import reactor.core.publisher.Mono

@HttpExchange(url = "/euser-sso/internal")
interface EuserSSOExchange {

    @GetExchange("/perms/public-api")
    fun queryPublicAPIPerms(@RequestParam("un") username: String): Mono<EuserSSOResp<QueryPublicAPIPermissionResponse>>
}