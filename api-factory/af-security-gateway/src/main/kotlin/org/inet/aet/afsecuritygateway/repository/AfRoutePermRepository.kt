package org.inet.aet.afsecuritygateway.repository

import com.github.benmanes.caffeine.cache.Cache
import org.inet.aet.afsecuritygateway.exchange.EuserSSOExchange
import org.inet.aet.afsecuritygateway.exchange.response.QueryPublicAPIPermissionResponse
import org.inet.aet.afsecuritygateway.exchange.response.EuserSSOResp
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import java.util.Collections

@Repository
class AfRoutePermRepository(
    afRedisConnectionFactory: RedisConnectionFactory,
    val afPermCache: Cache<String, List<String>>,
    val euserSSOExchange: EuserSSOExchange,
) {

    private final val redisTemplate: RedisTemplate<String, Any> = RedisTemplate()

    init {
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericJackson2JsonRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()
        redisTemplate.hashValueSerializer = GenericJackson2JsonRedisSerializer()
        redisTemplate.connectionFactory = afRedisConnectionFactory
        redisTemplate.afterPropertiesSet()
    }

    companion object {
        const val REDIS_KEY_PREFIX = "afu:"
    }

    fun queryPermissions(username: String?): Flux<String> {
        if (username == null) {
            return Flux.fromIterable(Collections.emptyList())
        }
        // 检查 local cache
        val localCachedValue = afPermCache.getIfPresent(username)
        if (localCachedValue != null) {
            println(localCachedValue)
            return Flux.fromIterable(localCachedValue)
        }
        // 检查 Redis
        val keyInRedis = REDIS_KEY_PREFIX + username
        val cachedValue = redisTemplate.opsForSet().members(keyInRedis)
        if (cachedValue != null) {
            if (cachedValue.isNotEmpty() || redisTemplate.hasKey(keyInRedis)) {
                val perms = cachedValue.parallelStream().map { v: Any -> v as String }.toList()
                println(perms)
                afPermCache.put(username, perms)
                return Flux.fromIterable(perms)
            }
        }
        // 发起远程调用
        return euserSSOExchange.queryPublicAPIPerms(username).map { resp: EuserSSOResp<QueryPublicAPIPermissionResponse>? -> resp?.data?.routes
            ?: Collections.emptyList() }.flatMapMany { routes -> Flux.fromIterable(routes) }
    }
}