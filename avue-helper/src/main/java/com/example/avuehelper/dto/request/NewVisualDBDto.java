package com.example.avuehelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建新数据源")
public class NewVisualDBDto {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 12, message = "username 长度要求 3-12")
    @Schema(description = "数据源名称")
    private String name;

    @NotBlank
    @Schema(description = "驱动类")
    private String driverClass;

    @NotBlank
    @Schema(description = "链接地址")
    private String url;

    @NotBlank
    @Schema(description = "用户名")
    private String username;

    @NotBlank
    @Schema(description = "密码")
    private String password;

    @Schema(description = "机构 ID")
    private Integer tenantId;

    @Schema(description = "备注")
    @Size(max = 200, message = "备注最长 200 字")
    private String remark;
}
