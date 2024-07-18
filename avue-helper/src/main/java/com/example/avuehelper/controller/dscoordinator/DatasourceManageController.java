package com.example.avuehelper.controller.dscoordinator;

import com.example.avuehelper.dto.request.dscoordinator.CheckConnRequest;
import com.example.avuehelper.dto.response.R;
import com.example.avuehelper.service.dscoordinator.DatasourceMangeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ds-manage")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "datasource 管理")
public class DatasourceManageController {

    private final DatasourceMangeService dsMangeService;

    @PostMapping("/check-conn")
    @Operation(summary = "检查数据源是否可以连接")
    public R<Boolean> checkConnection(@RequestBody @Valid CheckConnRequest body) {
        boolean success = dsMangeService.checkConnection(body.getDataSourceConf());
        return R.ok(success);
    }
}
