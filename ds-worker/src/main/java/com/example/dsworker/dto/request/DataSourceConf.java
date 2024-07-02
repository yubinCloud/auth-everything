package com.example.dsworker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "数据源的相关配置")
public class DataSourceConf {
    @NotBlank
    private String driverClass;

    @NotBlank
    private String url;

    private String username;

    private String password;
}
