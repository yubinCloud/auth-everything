package com.example.avuehelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "大屏的名字")
public class VisualName {

    @Schema(description = "大屏的名字")
    private String visualName;

    @Schema(description = "所属分类的名字")
    private String categoryName;
}
