package com.example.dsworker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ExecuteSQLRequest {
    @Schema(description = "数据源相关配置")
    private DataSourceConf dataSourceConf;

    @Schema(description = "要执行的 SQL 语句")
    private String sql;
}
