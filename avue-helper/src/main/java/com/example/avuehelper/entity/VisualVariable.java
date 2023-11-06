package com.example.avuehelper.entity;

import cn.hutool.json.JSONObject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "大屏的变量")
public class VisualVariable {

    @NotNull
    @Min(0)
    @Schema(description = "大屏的 ID")
    private long id;

    @NotNull
    @Schema(description = "变量")
    private JSONObject variable;
}
