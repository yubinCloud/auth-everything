package com.example.eusersso.dto.response;

import com.example.eusersso.entity.AvuePermission;
import com.example.eusersso.entity.PublicApiPermission;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "个人的权限信息")
public class PersonalPermissionInfo {

    @Schema(description = "avue 的权限信息")
    List<AvuePermission> avuePermissions;

    @Schema(description = "公开 API 的权限信息")
    List<PublicApiPermission> publicApiPermissions;
}
