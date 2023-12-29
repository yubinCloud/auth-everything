package com.example.afgateway.respository

import com.example.afgateway.exchange.EuserSSOExchange
import com.example.afgateway.exchange.response.QueryPublicAPIPermissionResponse
import com.example.afgateway.exchange.response.R
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Collections

@Repository
class AfRoutePermRepository(
    afRedisConnectionFactory: ReactiveRedisConnectionFactory,
    redisSerializationContext: RedisSerializationContext<String, Any>,
    val euserSSOExchange: EuserSSOExchange,
) {


    private final val reactiveRedisTemplate: ReactiveRedisTemplate<String, Any> = ReactiveRedisTemplate(afRedisConnectionFactory, redisSerializationContext)

    companion object {
        const val REDIS_KEY_PREFIX = "afu:"
    }


    @Cacheable(value = ["af-perm"], key = "#username")
    fun queryPermissions(username: String): Flux<String> {
        val keyInRedis = REDIS_KEY_PREFIX + username
        val cachedValue = reactiveRedisTemplate.opsForSet().members(keyInRedis);
        cachedValue.subscribe()
        if (cachedValue.isNotEmpty() || redisTemplate.hasKey(keyInRedis)) {
            return cachedValue.parallelStream().map { v: Any -> v as String }.toList()
        }
        return euserSSOExchange.queryPublicAPIPerms(username).map { resp: R<QueryPublicAPIPermissionResponse>? -> resp?.data?.routes
            ?: Collections.emptyList() }.flatMapMany { routes -> Flux.fromIterable(routes) }
    }
}