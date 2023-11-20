package com.example.dsworker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ExecuteSQLRequest {
    @Schema(description = "数据源相关配置")
    private DataSourceConf dataSourceConf;

    @Schema(description = "要执行的 SQL 语句")
    private String sql;

    @Schema(description = "SQL 中 `#{}` 要替换的值")
    private Map<String, SQLSlot> slots;
}
