package com.example.afleader.controller;

import com.example.afleader.dto.request.DsExecSelectSQLRequest;
import com.example.afleader.dto.response.R;
import com.example.afleader.exchange.DsWorkerExchange;
import com.example.afleader.exchange.request.dsworker.ExecuteSQLRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/ds")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "数据源相关接口")
public class DatasourceController {

    private final DsWorkerExchange dsWorkerExchange;

    @PostMapping("/sql/exec/select")
    @Operation(summary = "执行 SELECT 的 SQL 查询")
    public R<List<Map<String, Object>>> execSelectSQL(@RequestBody @Valid DsExecSelectSQLRequest body) {
        ExecuteSQLRequest executeSQLRequest = new ExecuteSQLRequest();
        executeSQLRequest.setSql(body.getSql());
        executeSQLRequest.setDatasourceConf(body.getDsConf());
        return dsWorkerExchange.executeSelectSQL(executeSQLRequest);
    }
}
