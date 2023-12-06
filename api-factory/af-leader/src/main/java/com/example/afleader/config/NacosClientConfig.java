package com.example.afleader.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NacosClientConfig {

    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;

    @Bean
    public NamingService namingService() throws NacosException {
        return NamingFactory.createNamingService(serverAddr);
    }
}
