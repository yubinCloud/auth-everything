package com.example.dsworker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "数据源的相关配置")
public class DataSourceConf {
    @Schema(description = "数据源的 ID，用于缓存数据库连接池，格式建议为：`<服务名>:<ID>` 来防止不同的服务调用产生冲突")
    private String id;

    @NotBlank
    private String driverClass;

    @NotBlank
    private String url;

    private String username;

    private String password;
}
