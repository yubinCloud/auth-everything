package com.example.ssoauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "删除用户权限的请求")
public class DeletePermParam {

    @NotNull
    @Schema(description = "所要删除的用户的用户名")
    private String username;

    @NotNull
    @Schema(description = "所要删除的权限")
    private String permission;
}
