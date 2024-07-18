package com.example.dsworker.service;

import com.example.dsworker.dto.request.DataSourceConf;
import com.example.dsworker.dto.response.adapter.dataease.TableField;
import com.example.dsworker.entity.DBSchema;
import com.example.dsworker.utils.ResultSetConverter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
            var tables = ResultSetConverter.toMapList(tablesRs);
            for (Map<String, Object> tableMap : tables) {
                String tableName = (String) tableMap.get("TABLE_NAME");
                String remark = (String) tableMap.get("REMARKS");
                var table = new DBSchema.DBTable();
                table.setName(tableName);
                table.setRemark(remark);
                table.setPrimaryKeys(Collections.emptyList());
                var columnRs = metadata.getColumns(catalog, "%", tableName, "%");
                var columns = ResultSetConverter.toMapList(columnRs)
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

    public List<TableField> getTableFields(DataSourceConf dsConf, String targetTable) throws SQLException {
        List<TableField> tableFields = new ArrayList<>();
        try (Connection conn = dataSourceService.getConnection(dsConf)) {
            var metadata = conn.getMetaData();
            var resultSet = metadata.getColumns(null, "%", targetTable, "%");
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String databaseName = resultSet.getString("TABLE_CAT");
                if (tableName.equalsIgnoreCase(targetTable)) {
                    TableField tableField = extractTableField(resultSet, dsConf);
                    tableFields.add(tableField);
                }
            }
            resultSet.close();
        }
        return tableFields;
    }

    private TableField extractTableField(ResultSet resultSet, DataSourceConf dsConf) throws SQLException {
        TableField tableField = new TableField();
        String colName = resultSet.getString("COLUMN_NAME");
        tableField.setFieldName(colName);
        String remarks = resultSet.getString("REMARKS");
        if (StringUtils.isAllBlank(remarks)) {
            remarks = colName;
        }
        tableField.setRemarks(remarks);
        String fieldType = resultSet.getString("TYPE_NAME").toUpperCase();
        tableField.setFieldType(fieldType);
        // 识别 fieldType 的 size（这里的识别有待改善）
        String fieldSizeString = resultSet.getString("COLUMN_SIZE");
        if (fieldSizeString == null) {
            tableField.setFieldSize(1);
        } else {
            tableField.setFieldSize(Integer.parseInt(fieldSizeString));
        }
        return tableField;
    }
}
