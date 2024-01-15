package com.example.dsworker.config.applicationrunner;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;


/**
 * 程序初始化时加载各个 JDBC Driver
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PreloadDriverClassApplicationRunner implements ApplicationRunner {

    private final Set<String> supportedDrivers;

    @Override
    public void run(ApplicationArguments args){
        supportedDrivers.parallelStream().forEach(driver -> {
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                log.error("Cannot find JDBC driver: " + driver);
            }
        });
    }
}
