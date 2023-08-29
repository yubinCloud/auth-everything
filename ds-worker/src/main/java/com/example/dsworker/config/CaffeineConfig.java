package com.example.dsworker.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CaffeineConfig {

    /**
     * 数据源的缓存
     * @return
     */
    @Bean
    public Cache<String, HikariDataSource> dataSourceCache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(60, TimeUnit.MINUTES)  // 一小时后过期
                .initialCapacity(30)    // 初始容量 30
                .maximumSize(200)       // 最大容量 200
                .removalListener((RemovalListener<String, HikariDataSource>) (key, dataSource, removalCause) -> {
                    if (dataSource != null) {
                        dataSource.close();
                    }
                })
                .build();
    }
}
