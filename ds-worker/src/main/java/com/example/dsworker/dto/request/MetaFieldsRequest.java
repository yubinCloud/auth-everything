package com.example.dsworker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "获取数据库表格的字段信息的请求")
public class MetaFieldsRequest {

    @Schema(description = "数据源属性")
    @NotNull
    private DataSourceConf dataSourceConf;

    @Schema(description = "数据库表名")
    @NotBlank(message = "表名不能为空")
    private String tableName;
}
