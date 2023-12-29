package org.inet.aet.afsecuritygateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "spring.data.af-redis")
data class AfRedisProperties(
    var database: Int = 1,
    var host: String = "localhost",
    var port: Int = 6379,
)
