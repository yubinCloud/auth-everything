package com.example.ssoauth.dto.request;

import com.example.ssoauth.validation.annotation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import java.util.List;

@Data
@Schema(description = "创建用户")
public class NewUserDto {
    @NotBlank
    @Size(min = 3, max = 12, message = "username 长度要求 3-12")
    @Pattern(regexp = "^[^\\s?&\\.\\\\\\/]*$", message = "username 不允许含有空白字符以及 ?&\\/. 等特殊符号。")
    @Schema(description = "用户名")
    private String username;

    @NotBlank
    @ValidPassword
    @Schema(description = "密码")
    private String password;

    @NotBlank
    @Schema(description = "显示的名称")
    private String screenName;

    @Range(min = 1, max = 3, message = "角色字段应该在 1-3 之间")
    @Schema(description = "角色：1：super-admin，2：normal，3： visitor")
    private int role;

    @Schema(description = "权限列表")
    private List<String> permissionList;

    @Schema(description = "是否具有 JupyterHub 的管理权限")
    private Boolean jupyterhubAdmin;

    @Schema(description = "用户描述/备注")
    private String note;
}
