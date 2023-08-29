package org.inet.aet.sidecar.jupyterservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JupyterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(JupyterServiceApplication.class, args);
    }

}
