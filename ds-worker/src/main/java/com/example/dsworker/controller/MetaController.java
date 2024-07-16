package com.example.dsworker.controller;

import com.example.dsworker.dto.request.DBSchemaRequest;
import com.example.dsworker.dto.request.DataSourceConf;
import com.example.dsworker.dto.request.MetaFieldsRequest;
import com.example.dsworker.dto.response.R;
import com.example.dsworker.entity.DBSchema;
import com.example.dsworker.service.DataSourceService;
import com.example.dsworker.service.MetadataService;
import com.example.dsworker.utils.ResultSetConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.sql.*;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/meta")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "获取 DB 的元数据的 API")
public class MetaController {

    private final DataSourceService dataSourceService;

    private final MetadataService metadataService;

    @PostMapping("/tables")
    @Operation(summary = "获取数据库的所有 tables")
    public R<List<Map<String, Object>>> getTables(
            @RequestBody @Valid DataSourceConf dsConf
    ) throws SQLException, ClassNotFoundException {
        List<Map<String, Object>> list;
        try (
                Connection conn = dataSourceService.getConnection(dsConf)
        ) {
            String catalog = conn.getCatalog();
            try (
                    ResultSet rs = conn.getMetaData().getTables(catalog, null, null, new String[]{"TABLE"})
            ) {
                list = ResultSetConverter.toMapList(rs);
            }
        }
        return R.ok(list);
    }

    @PostMapping("/fields")
    @Operation(summary = "获取数据库表的所有字段")
    public R<List<Map<String, Object>>> getFields(
            @RequestBody @Valid MetaFieldsRequest body
    ) throws SQLException {
        List<Map<String, Object>> list;
        try (
                Connection conn = dataSourceService.getConnection(body.getDataSourceConf())
        ) {
            String catalog = conn.getCatalog();
            try (
                    ResultSet rs = conn.getMetaData().getColumns(catalog, "%", body.getTableName(), "%")
            ) {
                list = ResultSetConverter.toMapList(rs);
            }
        }
        return R.ok(list);
    }

    @PostMapping("/db-schema")
    @Operation(summary = "获取一个 DB 的 schema 信息")
    public R<DBSchema> getDBSchema(@RequestBody @Valid DBSchemaRequest body) throws SQLException {
        DBSchema schema = metadataService.getDBSchema(body.getDataSourceConf());
        return R.ok(schema);
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/sso_auth";
        String user = "root";
        String pwd = "root";
        Connection conn = DriverManager.getConnection(url, user, pwd);
        String catalog = conn.getCatalog();  // db name
        var meta = conn.getMetaData();
        var rs = meta.getTables(catalog, "%", "%", new String[]{"TABLE"});
        System.out.println(ResultSetConverter.toMapList(rs));
        var colRs = meta.getColumns(catalog, "%", "role", "%");
        System.out.println(ResultSetConverter.toMapList(colRs));
        System.out.println(meta.getDatabaseProductName());
        System.out.println(meta.getDatabaseProductVersion());
        System.out.println(meta.getDriverName());
    }
}
