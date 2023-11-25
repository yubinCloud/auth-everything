package com.example.avuehelper.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioProperties {

    private String host;

    private String bucket;

    private String url;

    private String accessKey;

    private String secretKey;
}
