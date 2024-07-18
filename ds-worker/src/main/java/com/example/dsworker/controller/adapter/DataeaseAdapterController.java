package com.example.dsworker.controller.adapter;

import com.example.dsworker.dto.request.DataSourceConf;
import com.example.dsworker.dto.request.ExecuteSQLRequest;
import com.example.dsworker.dto.request.MetaFieldsRequest;
import com.example.dsworker.dto.response.R;
import com.example.dsworker.dto.response.adapter.dataease.DataAndFieldSet;
import com.example.dsworker.dto.response.adapter.dataease.TableField;
import com.example.dsworker.service.ExecuteService;
import com.example.dsworker.service.MetadataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/dataease-adapter")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "适配 dataease 的 API 接口")
public class DataeaseAdapterController {

    private final ExecuteService executeService;

    private final MetadataService metadataService;

    @PostMapping("/exec-select")
    @Operation(summary = "执行 SQL 查询语句")
    public R<List<String[]>> execSelectSQL(@RequestBody @Valid ExecuteSQLRequest body) throws SQLException, ClassNotFoundException {
        List<String[]> list = executeService.execQueryOfArrayFormat(body);
        return R.ok(list);
    }

    @PostMapping("/fetch-result-and-field")
    @Operation(summary = "执行 SQL 查询，获取 resultSet 以及相应 field 描述")
    public R<DataAndFieldSet> fetchResultAndField(@RequestBody @Valid ExecuteSQLRequest body) throws SQLException, ClassNotFoundException {
        var result = executeService.execQueryOfFieldFormat(body);
        return R.ok(result);
    }

    @PostMapping("/get-table-fields")
    @Operation(summary = "获取 table 的 fields，以及相应描述")
    public R<List<TableField>> getTableFields(@RequestBody @Valid MetaFieldsRequest body) throws SQLException, ClassNotFoundException {
        var result = metadataService.getTableFields(body.getDataSourceConf(), body.getTableName());
        return R.ok(result);
    }

}
