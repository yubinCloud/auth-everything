package org.inet.aet.sidecar.dscoordinator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DsCoordinatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(DsCoordinatorApplication.class, args);
    }

}
