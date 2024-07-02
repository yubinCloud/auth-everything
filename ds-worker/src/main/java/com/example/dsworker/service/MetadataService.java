package com.example.dsworker.service;

import com.example.dsworker.dto.request.DataSourceConf;
import com.example.dsworker.entity.DBSchema;
import com.example.dsworker.utils.ResultSetConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MetadataService {

    private final DataSourceService dataSourceService;

    public DBSchema getDBSchema(DataSourceConf dsConf) throws SQLException {
        DBSchema schema = new DBSchema();
        schema.setTables(new ArrayList<>());
        try (Connection conn = dataSourceService.getConnection(dsConf)) {
            String catalog = conn.getCatalog();
            schema.setDbName(catalog);
            var metadata = conn.getMetaData();
            var tablesRs = metadata.getTables(catalog, "%", "%", new String[]{"TABLE"});
            var tables = ResultSetConverter.toList(tablesRs);
            for (Map<String, Object> tableMap : tables) {
                String tableName = (String) tableMap.get("TABLE_NAME");
                String remark = (String) tableMap.get("REMARKS");
                var table = new DBSchema.DBTable();
                table.setName(tableName);
                table.setRemark(remark);
                table.setPrimaryKeys(Collections.emptyList());
                var columnRs = metadata.getColumns(catalog, "%", tableName, "%");
                var columns = ResultSetConverter.toList(columnRs)
                        .stream()
                        .map(colMap -> {
                            var col = new DBSchema.DBColumn();
                            col.setName((String) colMap.get("COLUMN_NAME"));
                            col.setTyp((String) colMap.get("TYPE_NAME"));
                            col.setNullable((int) colMap.get("NULLABLE"));
                            col.setRemark((String) colMap.get("REMARKS"));
                            return col;
                        })
                        .toList();
                table.setColumns(columns);
                schema.getTables().add(table);
            }
            schema.setDbProductName(metadata.getDatabaseProductName());
            schema.setDbProductVersion(metadata.getDatabaseProductVersion());
            schema.setDbDriverName(metadata.getDriverName());
        }
        return schema;
    }
}
