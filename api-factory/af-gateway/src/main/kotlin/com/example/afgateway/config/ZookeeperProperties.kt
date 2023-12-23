package com.example.afgateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "zookeeper")
data class ZookeeperProperties(
    val connectString: String,
    val connectionTimeout: String,
    val sessionTimeout: Int,
    val sleepMsBetweenRetry: Int,
    val maxRetries: Int,
    val namespace: String,
) {}