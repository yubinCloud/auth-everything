package com.example.eusersso.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新（增加） API 权限的参数")
public class UpdateAddPublicAPIPermissionRequest {

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "需要增加的 API 列表")
    @NotNull(message = "路径列表不能为空")
    private List<String> routes;
}
