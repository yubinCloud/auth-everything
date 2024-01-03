package org.inet.aet.afsecuritygateway.config

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CuratorFrameworkConfig(val zkProp: ZookeeperProperties) {

    @Bean
    fun curatorFramework(): CuratorFramework {
        val retryPolicy = ExponentialBackoffRetry(zkProp.sleepMsBetweenRetry, zkProp.maxRetries)
        val framework = CuratorFrameworkFactory.builder()
            .connectString(zkProp.connectString)
            .connectionTimeoutMs(zkProp.connectionTimeout)
            .sessionTimeoutMs(zkProp.sessionTimeout)
            .retryPolicy(retryPolicy)
            .namespace(zkProp.namespace)
            .build()
        framework.start()
        return framework
    }
}