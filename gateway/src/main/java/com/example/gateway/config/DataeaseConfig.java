package com.example.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "dataease")
@Data
public class DataeaseConfig {

    /**
     * 是否开启 dataease 子系统
     */
    private boolean enableSubsystem;

}
