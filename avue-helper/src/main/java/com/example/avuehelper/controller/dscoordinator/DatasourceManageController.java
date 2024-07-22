package com.example.avuehelper.controller.dscoordinator;

import com.example.avuehelper.constant.XRequestHeader;
import com.example.avuehelper.dto.request.NewVisualDBDto;
import com.example.avuehelper.dto.request.dscoordinator.CheckConnRequest;
import com.example.avuehelper.dto.response.DbTableItem;
import com.example.avuehelper.dto.response.PageResult;
import com.example.avuehelper.dto.response.R;
import com.example.avuehelper.entity.VisualDB;
import com.example.avuehelper.service.VisualDBService;
import com.example.avuehelper.service.dscoordinator.DatasourceMangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;


@RestController
@RequestMapping("/ds-manage")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "datasource 管理")
public class DatasourceManageController {

    private final DatasourceMangeService dsMangeService;
    private final VisualDBService visualDBService;

    @PostMapping("/check-conn")
    @Operation(summary = "检查数据源是否可以连接")
    public R<Boolean> checkConnection(@RequestBody @Valid CheckConnRequest body) {
        boolean success = dsMangeService.checkConnection(body.getDataSourceConf());
        return R.ok(success);
    }

    @PostMapping("/create-ds")
    @Operation(summary = "创建数据源")
    public R<Boolean> createDatasource(@RequestBody @Valid NewVisualDBDto body, @RequestHeader(XRequestHeader.X_TENANT_ID) int tenantId) {
        body.setTenantId(tenantId);
        int updateResult;
        try {
            updateResult = visualDBService.insertOne(body);
        } catch (Exception e) {
            log.error(e.getMessage().strip());
            return R.badRequest("插入失败，请重新尝试", false);
        }
        return updateResult > 0? R.ok(true): R.ok(false);
    }

    @GetMapping("/ds-list")
    @Operation(summary = "数据源列表")
    public R<PageResult<VisualDB>> queryDatasourceList(@RequestParam("page_num") Integer pageNum, @RequestParam("page_size") Integer pageSize, @RequestHeader(XRequestHeader.X_TENANT_ID) int tenantId) {
        if (pageSize == null) {
            pageSize = 10;
        }
        if (pageNum == null) {
            pageNum = 1;
        }
        var result = visualDBService.queryPage(pageNum, pageSize, tenantId);
        return R.ok(result);
    }

    @PostMapping("/update-ds")
    @Operation(summary = "（TODO）修改数据源")
    public R<Boolean> updateDatasource(@RequestBody @Valid VisualDB visualDB, @RequestHeader(XRequestHeader.X_TENANT_ID) int tenantId) {
        return R.ok(true);
    }

    @DeleteMapping("/delete-ds/{id}")
    @Operation(summary = "（TODO）删除数据源")
    public R<Boolean> deleteDatasource(@PathVariable("id") long dsId, @RequestHeader(XRequestHeader.X_TENANT_ID) int tenantId) {
        return R.ok(true);
    }

    @GetMapping("/db-tables/{id}")
    @Operation(summary = "（TODO）获取一个 DB 的所有 tables")
    public R<List<DbTableItem>> getDBTables(@PathVariable("id") long dsId, @RequestHeader(XRequestHeader.X_TENANT_ID) int tenantId) {
        return R.ok(Collections.emptyList());
    }


}
