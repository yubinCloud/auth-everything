package com.example.afleader.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量查询路由信息的请求")
public class QueryBatchRouteInfoRequest {

    @Schema(description = "所需要查询的所有路由的路径")
    @NotNull(message = "路径列表不允许为空")
    private List<String> paths;
}
