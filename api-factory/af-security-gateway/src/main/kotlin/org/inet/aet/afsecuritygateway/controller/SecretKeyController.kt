package org.inet.aet.afsecuritygateway.controller

import org.inet.aet.afsecuritygateway.dto.response.R
import org.inet.aet.afsecuritygateway.dto.response.R_FORBIIDEN
import org.inet.aet.afsecuritygateway.dto.response.R_SUCCESS
import org.inet.aet.afsecuritygateway.repository.AfRoutePermRepository
import org.inet.aet.afsecuritygateway.service.EncryptService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/secret")
class SecretKeyController(val encryptService: EncryptService, val afRoutePermRepository: AfRoutePermRepository) {

    @GetMapping("/key")
    fun querySecretKey(@RequestParam("routePath") routePath: String, @RequestHeader("X-Euser") username: String): Mono<R<String?>> {
        return afRoutePermRepository.queryPermissions(username)
            .any(routePath::equals)
            .flatMap { valid: Boolean -> run {
                if (!valid) {
                    return@flatMap Mono.just(R_FORBIIDEN("权限不足", null))
                }
                val secretKey = encryptService.findRouteLocal(routePath).secretKey
                return@flatMap Mono.just(if (secretKey != null) R_SUCCESS(String(secretKey)) else R_SUCCESS(null))
            } }
    }
}