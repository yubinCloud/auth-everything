package com.example.afleader.dto.response;

import com.example.afleader.entity.RouteZnodeData;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "路由信息")
public class RouteInfoResponse {

    @Schema(description = "路由的基本信息（即存放于 znode 中的信息）")
    private RouteZnodeData basic;

    @Schema(description = "是否开启")
    private boolean enabled;

    @Schema(description = "是否加密")
    private boolean encrypted;
}
