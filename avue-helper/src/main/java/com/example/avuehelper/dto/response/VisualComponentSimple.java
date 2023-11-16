package com.example.avuehelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "大屏组件的简要信息")
public class VisualComponentSimple {

    private String title;

    private String name;

    @Schema(description = "组件 ID")
    private String index;
}
