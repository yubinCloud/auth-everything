package com.example.ssoauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "新增权限的参数")
public class AddPermParam {

    @NotNull
    private String username;

    @NotNull
    private List<String> permissionList;
}
