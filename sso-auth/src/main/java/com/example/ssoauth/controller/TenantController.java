package com.example.ssoauth.controller;

import com.example.ssoauth.dao.param.UserSelectCond;
import com.example.ssoauth.dto.request.DeleteTenantReq;
import com.example.ssoauth.dto.request.DeleteUserReq;
import com.example.ssoauth.dto.request.NewTenantDto;
import com.example.ssoauth.dto.request.NewUserDto;
import com.example.ssoauth.dto.response.PageResp;
import com.example.ssoauth.dto.response.R;
import com.example.ssoauth.entity.Tenant;
import com.example.ssoauth.entity.User;
import com.example.ssoauth.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tenant")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "租户管理接口",
        description = "包括新增、删除租户等操作,仅对 SUPER-ADMIN 开放"
)
public class TenantController {

    private final TenantService tenantService;

    @PostMapping("/add")
    @Operation(summary = "添加租户")
    public R<String> addTenant(
            @RequestBody @Valid NewTenantDto tenantDto) {
        tenantService.addTenant(tenantDto);
        return R.ok("add success");
    }

    @PostMapping("/delete")
    @Operation(summary = "删除租户")
    public R<String> deleteTenant(@RequestBody @Valid DeleteTenantReq req) {
        tenantService.deleteByName(req.getName());
        return R.ok("delete success");
    }

    @GetMapping("/list")
    @Operation(summary = "查看租户列表")
    public R<PageResp<Tenant>> findTenants(
            @Min(1) @Parameter(description = "页码", example = "1") @RequestParam(required = false, defaultValue = "1") int pageNum,
            @Min(1) @Parameter(description = "页大小", example = "10") @RequestParam(required = false, defaultValue = "10") int pageSize,
            @Parameter(description = "过滤条件：description，支持模糊搜索") @RequestParam(required = false) String name
    ) {

        PageResp<Tenant> pageResp = tenantService.selectByPage(pageNum,pageSize,name);

        return R.ok(pageResp);
    }
}
