package com.example.dsworker.controller;

import com.example.dsworker.dto.request.DataSourceConf;
import com.example.dsworker.dto.request.MetaFieldsRequest;
import com.example.dsworker.dto.response.R;
import com.example.dsworker.service.DataSourceService;
import com.example.dsworker.utils.ResultSetConverter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/meta")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "数据库元数据的操作"
)
public class MetaController {

    private final DataSourceService dataSourceService;

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
                list = ResultSetConverter.toList(rs);
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
                list = ResultSetConverter.toList(rs);
            }
        }
        return R.ok(list);
    }
}
