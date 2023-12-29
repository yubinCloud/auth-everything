package org.inet.aet.afsecuritygateway.config

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration
class CaffeineConfig {

    /**
     * 缓存用户的权限信息
     */
    @Bean
    fun afPermCache(): Cache<String, List<String>> {
        return Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .initialCapacity(30)
            .maximumSize(100)
            .build()
    }

    /**
     * 缓存 API 加密的信息
     */
    @Bean
    fun apiEncryptCache(): Cache<String, String?> {
        return Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .initialCapacity(30)
            .maximumSize(100)
            .build()
    }
}