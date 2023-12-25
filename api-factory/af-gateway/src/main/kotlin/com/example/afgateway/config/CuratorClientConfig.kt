package com.example.afgateway.config

import org.apache.curator.framework.CuratorFramework
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CuratorClientConfig(val zookeeperProperties: ZookeeperProperties) {

    @Bean
    fun curatorClient(): CuratorFramework {
        val retryPolicy = ExponentialBackoffRetry(zookeeperProperties.sleepMsBetweenRetry, zookeeperProperties.maxRetries)
        val curatorFramework = CuratorFrameworkFactory.builder()
            .connectString(zookeeperProperties.connectString)
            .connectionTimeoutMs(zookeeperProperties.connectionTimeout)
            .sessionTimeoutMs(zookeeperProperties.sessionTimeout)
            .retryPolicy(retryPolicy)
            .namespace(zookeeperProperties.namespace)
            .build()
        curatorFramework.start()
        return curatorFramework
    }
}