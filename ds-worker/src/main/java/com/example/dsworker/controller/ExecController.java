package com.example.dsworker.controller;

import com.example.dsworker.dto.request.ExecuteSQLRequest;
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
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/exec")
@RequiredArgsConstructor
@Slf4j
@Tag(
        name = "执行 SQL 语句"
)
public class ExecController {

    private final DataSourceService dataSourceService;

    @PostMapping("/select")
    @Operation(summary = "执行 SQL 查询语句")
    public R<List<Map<String, Object>>> executeSelectSQL(
            @RequestBody @Valid ExecuteSQLRequest body
    ) throws SQLException {
        List<Map<String, Object>> list;
        try (
                Connection conn = dataSourceService.getConnection(body.getDataSourceConf());
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(body.getSql())
                ) {
            list = ResultSetConverter.toList(rs);
            conn.commit();
        }
        return R.ok(list);
    }

    @PostMapping("/update")
    @Operation(summary = "执行 SQL 增删改相关语句")
    public R<Integer> executeUpdateSQL(
            @RequestBody @Valid ExecuteSQLRequest body
    ) throws SQLException {
        int count = 0;
        try (
                Connection conn = dataSourceService.getConnection(body.getDataSourceConf());
                Statement statement = conn.createStatement()
                ) {
            count = statement.executeUpdate(body.getSql());
            conn.commit();
        }
        return R.ok(count);
    }
}
