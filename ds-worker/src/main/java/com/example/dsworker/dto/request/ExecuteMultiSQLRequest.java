package com.example.dsworker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ExecuteMultiSQLRequest {

    @Schema(description = "数据源相关配置")
    @NotNull(message = "数据源配置不允许为 null")
    private DataSourceConf dataSourceConf;

    @Schema(description = "多个 SQL 语句")
    @NotNull(message = "multi SQL 不允许为 null")
    private List<SQLItem> multi;
}
