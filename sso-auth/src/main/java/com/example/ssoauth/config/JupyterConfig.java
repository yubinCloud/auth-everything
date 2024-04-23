package com.example.ssoauth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jupyter")
@Data
public class JupyterConfig {

    /**
     * 是否开启 jupyter 子系统
     */
    private boolean enableSubsystem;

}
