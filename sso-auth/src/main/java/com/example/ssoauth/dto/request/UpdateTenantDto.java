package com.example.ssoauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "修改租户的接口")
public class UpdateTenantDto {
    @Schema(description = "需要修改的租户的 ID")
    @NotNull(message = "租户 ID 不允许为空")
    private Integer tenantId;

    @Schema(description = "要修改的租户名称")
    @NotNull(message = "租户名不允许为空")
    private String name;
}
