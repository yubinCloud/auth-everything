package com.example.afgateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "zookeeper")
data class ZookeeperProperties(
    val connectString: String = "localhost:2181",
    val connectionTimeout: Int = 5000,
    val sessionTimeout: Int = 5000,
    val sleepMsBetweenRetry: Int = 1000,
    val maxRetries: Int = 3,
    val namespace: String = "af",
)