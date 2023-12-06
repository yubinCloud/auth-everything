package com.example.afleader;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class AfLeaderApplication {

    public static void main(String[] args) {
        SpringApplication.run(AfLeaderApplication.class, args);
    }

}
