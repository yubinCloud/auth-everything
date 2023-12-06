package com.example.afleader.controller;

import com.example.afleader.dto.request.*;
import com.example.afleader.dto.response.RouteCreateResponse;
import com.example.afleader.service.DynamicRouteService;
import com.example.afleader.dto.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dynamic-route")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "动态路由管理")
public class DynamicRouteController {

    private final DynamicRouteService dynamicRouteService;

    @PostMapping("/create/sql")
    @Operation(summary = "创建 SQL 类型的动态路由")
    public R<RouteCreateResponse> createSQLRoute(@RequestBody @Valid SQLRouteCreateRequest body) throws Exception {
        var resp = dynamicRouteService.createSQLRoute(body);
        return R.ok(resp);
    }

    @PostMapping("/create/common")
    @Operation(summary = "创建自定义函数的动态路由")
    public R<RouteCreateResponse> createCommonRoute(@RequestBody @Valid CommonRouteCreateRequest body) throws Exception {
        var resp = dynamicRouteService.createCommonRoute(body);
        return R.ok(resp);
    }

    @PostMapping("/create/http")
    @Operation(summary = "创建 HTTP 类型的动态路由")
    public R<RouteCreateResponse> createHttpRoute() {
        throw new NotImplementedException();
    }

    @PostMapping("/enable")
    @Operation(summary = "修改动态路由是否*开启*")
    public R<String> enableRoute(@RequestBody @Valid EnableRouteRequest body) throws Exception {
        boolean success = dynamicRouteService.enableRoute(body.getRoutePath(), body.isEnable());
        return success? R.ok("success"): R.error("该路由不存在", "fail");
    }

    @PostMapping("/encrypt")
    @Operation(summary = "修改动态路由是否*加密*")
    public R<String> encryptRoute(@RequestBody @Valid EncryptRouteRequest body) throws Exception {
        boolean success = dynamicRouteService.encryptRoute(body.getPath(), body.isEncrypt());
        return success? R.ok("success"): R.error("该路由不存在", "fail");
    }

    @GetMapping("/info")
    @Operation(summary = "查看一个 route 的信息")
    public R<Object> getRouteInfo() {
        // TODO
        throw new NotImplementedException();
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除一个路由")
    public R<String> deleteRoute(@RequestBody @Valid DeleteRouteRequest body) throws Exception {
        boolean success = dynamicRouteService.deleteRoute(body.getPath());
        return success? R.ok("success"): R.error("删除失败", "fail");
    }

    @PostMapping("/refresh/one")
    @Operation(summary = "刷新一个 Route")
    public R<String> refreshOne() {
        // TODO
        throw new NotImplementedException();
    }

    @PostMapping("/refresh/all")
    @Operation(summary = "刷新所有 Route")
    public R<String> refreshAll() {
        // TODO
        throw new NotImplementedException();
    }
}
