package com.example.eusersso.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "一个 avue 大屏的权限信息")
public class AvuePermission {

    @Schema(description = "大屏 ID")
    private Long visualId;

    @Schema(description = "能看到的组件 ID 列表")
    private List<String> whitelist;
}
