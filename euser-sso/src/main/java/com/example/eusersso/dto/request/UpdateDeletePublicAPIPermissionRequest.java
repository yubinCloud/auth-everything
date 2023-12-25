package com.example.eusersso.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "更新（删除） API 权限的参数")
public class UpdateDeletePublicAPIPermissionRequest {

    @Schema(description = "用户名")
    @NotBlank(message = "用户名不能为空")
    private String username;

    @Schema(description = "待删除的 API")
    @NotBlank(message = "路径名不能为空")
    private String route;

}
