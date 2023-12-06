package com.example.afleader.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "创建普通类型的 Route 的请求参数")
public class CommonRouteCreateRequest {

    @Schema(description = "API 路径")
    @Pattern(regexp = "[A-Za-z0-9-/]+", message = "API 路径只允许使用数字、英文字母、英文连字符和正斜线，例如 `/example/query-1`")
    @Size(min = 1, max = 30, message = "API 路径最长 30 个字符")
    @NotBlank(message = "API 路径不允许为空")
    private String path;

    @Schema(description = "API 名称")
    @Size(max = 20, message = "API 名称最长 20 个字符")
    private String name;

    @Schema(description = "是否加密")
    private Boolean encrypt;

    @Schema(description = "代码")
    @NotBlank(message = "代码不允许为空")
    @Size(max = 3072, message = "代码长度不允许超过 3072 个字符")
    private String code;
}
