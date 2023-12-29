package com.example.afgateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.data.af-redis")
data class AfRedisProperties(
    val database: Int = 0,
    val host: String = "localhost",
    val port: Int = 6379,
)
