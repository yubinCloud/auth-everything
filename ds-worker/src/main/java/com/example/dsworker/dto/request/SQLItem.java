package com.example.dsworker.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "请求中一个 SQL 的内容，包括原始语句以及 slots")
public class SQLItem {

    @Schema(description = "原始 SQL 语句")
    private String sql;

    @Schema(description = "用于填充 SQL 的 slots")
    private List<SQLSlot> slots;
}
