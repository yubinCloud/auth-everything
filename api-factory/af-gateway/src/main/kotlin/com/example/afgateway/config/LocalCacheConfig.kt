package com.example.afgateway.config

import com.github.benmanes.caffeine.cache.Caffeine
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import org.springframework.cache.support.SimpleCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

/**
 * 设置本地缓存
 */
@Configuration
class LocalCacheConfig {

    @Bean
    fun cacheManager(): CacheManager {
        val manager = SimpleCacheManager()
        val caches: List<Cache> = ArrayList()
        // 存放 public api 权限信息的 cache
        caches.addLast(CaffeineCache("af-perm", Caffeine.newBuilder().recordStats().expireAfterWrite(60, TimeUnit.SECONDS).maximumSize(100).build()))
        manager.setCaches(caches)
        return manager
    }
}