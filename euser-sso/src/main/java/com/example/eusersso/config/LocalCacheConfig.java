package com.example.eusersso.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class LocalCacheConfig {

    private final ShanDongTongProperties shanDongTongProperties;

    @Bean
    public Cache<String, String> shanDongTongAccessTokenCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .initialCapacity(1)
                .maximumSize(1)
                .build();
    }

}
