package com.example.dsworker.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class ExecuteMultiSQLResponse {

    @Schema(description = "执行多个 SQL 的结果")
    private List<ExecuteSQLResult> execResults;

}
