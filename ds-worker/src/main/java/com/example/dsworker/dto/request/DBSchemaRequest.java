package com.example.dsworker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DBSchemaRequest {

    @Schema(description = "数据源信息")
    private DataSourceConf dataSourceConf;
}
