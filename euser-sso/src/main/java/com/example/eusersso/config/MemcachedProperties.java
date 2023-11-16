package com.example.eusersso.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "memcached")
@Data
public class MemcachedProperties {

    private String host;

    private int port;

    private String username;

    private String password;
}
