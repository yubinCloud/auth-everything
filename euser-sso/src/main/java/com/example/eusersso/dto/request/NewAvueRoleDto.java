package com.example.eusersso.dto.request;

import com.example.eusersso.entity.AvuePermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "新建用户的信息")
public class NewAvueRoleDto {

    @Schema(description = "角色名")
    private String name;

    @Schema(description = "avue 大屏权限信息")
    private List<AvuePermission> permissions;

    @Schema(description = "系统首页")
    private List<Long> sysHomes;
}
