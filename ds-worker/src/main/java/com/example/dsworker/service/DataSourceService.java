package com.example.dsworker.service;

import com.example.dsworker.dto.request.DataSourceConf;
import com.example.dsworker.exception.DatabaseDriverFoundException;
import com.example.dsworker.mapstruct.DataSourceConfConverter;
import com.github.benmanes.caffeine.cache.Cache;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DataSourceService {

    private final Cache<String, HikariDataSource> dataSourceCache;

    private final DataSourceConfConverter dsConfConverter;

    private final Set<String> supportedDrivers;

    public Connection getConnection(DataSourceConf conf) throws SQLException {
        String id = conf.getId();
        Connection conn;
        if (id == null) {
            if (Objects.equals(conf.getDriverClass(), "X-Inceptor")) {
                conf.setDriverClass("org.apache.hive.jdbc.HiveDriver");
            }
            if (!supportedDrivers.contains(conf.getDriverClass())) {
                throw new DatabaseDriverFoundException(conf.getDriverClass());
            }
            if (Objects.nonNull(conf.getUsername())) {
                conn = DriverManager.getConnection(conf.getUrl(), conf.getUsername(), conf.getPassword());
            }
            else {
                conn = DriverManager.getConnection(conf.getUrl());
            }
        } else {
            var ds = dataSourceCache.getIfPresent(conf.getId());
            if (ds == null) {
                ds = hikariDataSourceFactory(conf);
                dataSourceCache.put(conf.getId(), ds);
            }
            conn = ds.getConnection();
        }
        conn.setAutoCommit(false);
        return conn;
    }

    private HikariDataSource hikariDataSourceFactory(DataSourceConf conf) {
        HikariConfig config = dsConfConverter.toHikariConf(conf);
        return new HikariDataSource(config);
    }

}
