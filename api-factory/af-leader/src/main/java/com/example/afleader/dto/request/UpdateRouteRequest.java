package com.example.afleader.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
@Schema(description = "修改路由的参数")
public class UpdateRouteRequest {

    @Schema(description = "所需修改路由的路径")
    @NotBlank(message = "路由路径不可以为空")
    private String routePath;

    @Schema(description = "修改后的路由名。若为空则表示保持不变")
    @Size(max = 20, message = "路由名称最长 20 个字符")
    private String routeName;

    @Schema(description = "修改后的所支持的请求方法，比如 GET、POST。若为空则表示保持不变")
    private List<String> methods;

    @Schema(description = "修改后的代码。若为空则表示保持不变")
    @Size(max = 3072, message = "代码长度不允许超过 3072 个字符")
    private String code;
}
