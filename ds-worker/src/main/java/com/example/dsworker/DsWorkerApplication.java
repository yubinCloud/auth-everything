package com.example.dsworker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DsWorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DsWorkerApplication.class, args);
    }

}
