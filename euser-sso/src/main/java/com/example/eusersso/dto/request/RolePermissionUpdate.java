package com.example.eusersso.dto.request;

import com.example.eusersso.entity.AvuePermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "更新角色的权限信息")
public class RolePermissionUpdate {

    @Schema(description = "角色 ID")
    private int roleId;

    @Schema(description = "修改后的权限信息")
    private List<AvuePermission> permissions;
}
