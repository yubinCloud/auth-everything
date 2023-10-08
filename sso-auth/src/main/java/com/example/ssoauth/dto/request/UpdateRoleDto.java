package com.example.ssoauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "修改角色信息的接口")
public class UpdateRoleDto {
    @Schema(description = "需要修改的角色的 ID")
    @NotNull(message = "角色 ID 不允许为空")
    private Integer roleId;

    @Schema(description = "要修改的名称，null 表示不修改")
    private String name;

    @Schema(description = "要添加的权限列表，不允许为 null")
    @NotNull(message = "列表不允许为空")
    private List<String> add;

    @Schema(description = "要删除的权限列表，不允许为 null")
    @NotNull(message = "列表不允许为空")
    private List<String> del;
}