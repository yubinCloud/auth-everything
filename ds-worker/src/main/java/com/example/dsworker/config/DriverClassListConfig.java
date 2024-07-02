package com.example.dsworker.config;

import com.alibaba.nacos.common.utils.ConcurrentHashSet;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class DriverClassListConfig {

    private static final Set<String> DRIVER_LIST = Set.of(
            "org.postgresql.Driver",            // PG
            "oracle.jdbc.driver.OracleDriver",         // Oracle
            "com.mysql.cj.jdbc.Driver",         // MySQL
//            "org.apache.hive.jdbc.HiveDriver",  // Hive or Inceptor
            "com.microsoft.sqlserver.jdbc.SQLServerDriver",  // SQL Server
            "com.dbcp.jdbc.Driver"  // 航天云网 TyDB
    );

    @Bean
    public Set<String> supportedDrivers() {
        Set<String> drivers = new ConcurrentHashSet<>();
        DRIVER_LIST.parallelStream().forEach(drivers::add);
        return drivers;
    }

}
