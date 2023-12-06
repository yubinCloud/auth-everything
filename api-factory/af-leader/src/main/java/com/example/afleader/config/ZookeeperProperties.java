package com.example.afleader.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zookeeper")
@Data
public class ZookeeperProperties {

    /**
     * 集群地址，使用逗号分隔，不能有空格
     */
    private String connectString;

    /**
     * 连接超时时间
     */
    private int connectionTimeout;

    /**
     * 会话存活时间
     */
    private Integer sessionTimout;

    /**
     * 重试机制时间参数
     */
    private Integer sleepMsBetweenRetry;

    /**
     * 重试机制时间参数
     */
    private Integer maxRetries;

    /**
     * 命令空间（父节点名称）
     * 使用命名空间后，`client.create().forPath("/test", data)` 会实际创建在 `/namespace/test` 下
     */
    private String namespace;
}
