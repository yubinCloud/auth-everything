package com.example.ssoauth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SsoAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoAuthApplication.class, args);
    }

}
