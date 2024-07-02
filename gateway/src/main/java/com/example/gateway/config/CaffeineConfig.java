package com.example.gateway.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    /**
     * Jupyter Token 的缓存
     */
    @Bean
    public Cache<String, String> jupyterTokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .initialCapacity(30)
                .maximumSize(500)
                .build();
    }

    @Bean
    public Cache<String, List<String>> roleCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .initialCapacity(30)
                .maximumSize(500)
                .build();
    }

    @Bean
    public Cache<String, List<String>> permissionCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .initialCapacity(30)
                .maximumSize(500)
                .build();
    }

    /**
     * 缓存 dataease-backend 服务所需 token 的 cache
     * @return
     */
    @Bean
    public Cache<String, String> dataeaseTokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .initialCapacity(30)
                .maximumSize(500)
                .build();
    }
}
