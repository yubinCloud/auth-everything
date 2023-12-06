package com.example.afleader.config;

import lombok.RequiredArgsConstructor;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CuratorClientConfig {

    private final ZookeeperProperties zookeeperProperties;

    @Bean
    public CuratorFramework curatorClient() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(zookeeperProperties.getSleepMsBetweenRetry(), zookeeperProperties.getMaxRetries());
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(zookeeperProperties.getConnectString())
                .connectionTimeoutMs(zookeeperProperties.getConnectionTimeout())
                .sessionTimeoutMs(zookeeperProperties.getSessionTimout())
                .retryPolicy(retryPolicy)
                .namespace(zookeeperProperties.getNamespace())
                .build();
        client.start();
        return client;
    }

}
