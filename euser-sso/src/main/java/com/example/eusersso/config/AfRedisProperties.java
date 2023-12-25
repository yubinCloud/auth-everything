package com.example.eusersso.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "spring.data.af-redis"
)
@Data
public class AfRedisProperties {

    private int database;

    private String host;

    private int port;
}
