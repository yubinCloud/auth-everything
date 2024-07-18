package com.example.dsworker.service.adapter.dataease;

import com.example.dsworker.dto.request.DataSourceConf;
import com.example.dsworker.service.DataSourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DataeaseAdapterService {

    private final DataSourceService dataSourceService;

    public List<String> getSchema(DataSourceConf dsConf, String schemaSQL) throws SQLException, ClassNotFoundException {
        Class.forName(dsConf.getDriverClass());
        List<String> schemas = new ArrayList<>();
        try (Connection conn = dataSourceService.getConnection(dsConf);
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(schemaSQL)
        ) {
            while (rs.next()) {
                schemas.add(rs.getString(1));
            }
        }
        return schemas;
    }
}
