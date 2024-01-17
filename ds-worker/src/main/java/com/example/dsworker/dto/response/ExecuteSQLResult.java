package com.example.dsworker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Schema(description = "SQL 执行结果")
public class ExecuteSQLResult {

    private boolean isQuery;

    private int count;

    private List<Map<String, Object>> rows;

}
