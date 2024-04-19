package com.example.eusersso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties
@EnableFeignClients
public class EuserSsoApplication {

    public static void main(String[] args) {
        SpringApplication.run(EuserSsoApplication.class, args);
    }

}
