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

@Service
@RequiredArgsConstructor
public class DataSourceService {

    private final Cache<String, HikariDataSource> dataSourceCache;

    private final DataSourceConfConverter dsConfConverter;

    public Connection getConnection(DataSourceConf conf) throws SQLException {
        String id = conf.getId();
        Connection conn;
        if (id == null) {
            try {
                Class.forName(conf.getDriverClass());
            } catch (ClassNotFoundException e) {
                throw new DatabaseDriverFoundException(conf.getDriverClass());
            }
            conn = DriverManager.getConnection(conf.getUrl(), conf.getUsername(), conf.getPassword());
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
