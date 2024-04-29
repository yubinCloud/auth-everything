package com.example.ssoauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "创建新租户")
public class NewTenantDto {

    @NotBlank
    @Schema(description = "机构名")
    private String name;
}
