package com.example.eusersso.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

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

    @Size(max = 200, message = "备注最长 200 字")
    @Schema(description = "备注")
    private String note;
}
