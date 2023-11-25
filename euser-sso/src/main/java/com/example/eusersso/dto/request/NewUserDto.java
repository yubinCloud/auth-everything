package com.example.eusersso.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "创建新用户的信息")
public class NewUserDto {

    @NotBlank
    @Size(min = 3, max = 13, message = "username 长度要求 3-13")
    @Pattern(regexp = "^[^\\s?&\\.\\\\\\/]*$", message = "username 不允许含有空白字符以及 ?&\\/. 等特殊符号。")
    @Schema(description = "用户名")
    private String username;

    @NotBlank
    @Schema(description = "密码")
    private String password;

    @NotBlank
    @Pattern(regexp = "^[^\\s?&\\.\\\\\\/]*$", message = "显示名称中不允许含有空白字符以及 ?&\\/. 等特殊符号。")
    @Size(min = 3, max = 13, message = "显示名称长度要求 3-13")
    @Schema(description = "显示名称")
    private String screenName;

    @Schema(description = "是否允许立即使用")
    private boolean checked;

    @Schema(description = "分配的角色列表，元素必须为角色 ID")
    private List<Integer> avueRoles;

    @Size(max = 200, message = "备注最长 200 字")
    @Schema(description = "备注")
    private String note;

    @NotBlank(message = "手机号不能为空")
    @Size(max = 25, message = "手机号最长 30 字符")
    @Schema(description = "手机号")
    private String mobile;
}
