package org.inet.aet.afsecuritygateway.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@Configuration
@ConfigurationProperties(prefix = "zookeeper")
data class ZookeeperProperties (
    var connectString: String = "localhost:2181",
    var connectionTimeout: Int = 5000,
    var sessionTimeout: Int = 5000,
    var sleepMsBetweenRetry: Int = 1000,
    var maxRetries: Int = 3,
    var namespace: String = "af",
)