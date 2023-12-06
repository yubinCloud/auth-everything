package com.example.afleader.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "开启 / 关闭动态路由的请求参数")
public class EnableRouteRequest {

    @Schema(description = "开启 or 关闭")
    private boolean enable;

    @Schema(description = "route 路径")
    @NotBlank
    private String routePath;

}
