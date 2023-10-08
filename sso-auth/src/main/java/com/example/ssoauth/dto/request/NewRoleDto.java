package com.example.ssoauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建新角色")
public class NewRoleDto {

    @NotBlank(message = "角色名不能为空")
    private String name;

    private List<String> permissionList;
}
