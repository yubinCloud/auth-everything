package com.example.ssoauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "创建新租户")
public class NewTenantDto {
    private Integer tenantId;
    private String name;
}
