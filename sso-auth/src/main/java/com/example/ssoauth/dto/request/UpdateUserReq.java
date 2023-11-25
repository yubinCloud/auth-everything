package com.example.ssoauth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "修改用户信息的参数")
public class UpdateUserReq {

    @NotNull
    @Schema(description = "要修改的用户的用户名")
    private String username;

    @Schema(description = "修改的密码")
    private String password;

    @Schema(description = "修改的用户显示名")
    private String screenName;

    @Schema(description = "修改是否具有 Jupyter Hub 的管理权限")
    private Boolean jupyterhubAdmin;

    @Schema(description = "用户的描述/备注")
    private String note;

    @Schema(description = "修改的手机号")
    private String mobile;
}
