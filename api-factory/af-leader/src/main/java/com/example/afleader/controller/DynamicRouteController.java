package com.example.afleader.controller;

import com.example.afleader.dto.request.*;
import com.example.afleader.dto.response.RouteCreateResponse;
import com.example.afleader.dto.response.RouteInfoResponse;
import com.example.afleader.service.AfWorkerService;
import com.example.afleader.service.DynamicRouteService;
import com.example.afleader.dto.response.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/dynamic-route")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "动态路由管理")
public class DynamicRouteController {

    private final DynamicRouteService dynamicRouteService;

    private final AfWorkerService afWorkerService;

    @PostMapping("/create/sql")
    @Operation(summary = "创建 SQL 类型的动态路由")
    public R<RouteCreateResponse> createSQLRoute(@RequestBody @Valid SQLRouteCreateRequest body) throws Exception {
        log.info("recv request");
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
    public R<RouteInfoResponse> getRouteInfo(
            @NotBlank(message = "路由路径不允许为空") @Parameter(description = "路由路径", required = true) @RequestParam String routePath
    ) {
        var resp = dynamicRouteService.getRouteInfo(routePath);
        return Objects.nonNull(resp)? R.ok(resp): R.error("路由信息读取出错", null);
    }

    @PostMapping("/update")
    @Operation(summary = "更新一个路由")
    public R<String> updateRoute(@RequestBody @Valid UpdateRouteRequest body) throws Exception {
        boolean success = dynamicRouteService.updateRoute(body.getRoutePath(), body.getRouteName(), body.getMethods(), body.getCode());
        return success? R.ok("success"): R.error("未找到该路由路径，更新失败", "fail");
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除一个路由")
    public R<String> deleteRoute(@RequestBody @Valid DeleteRouteRequest body) throws Exception {
        boolean success = dynamicRouteService.deleteRoute(body.getPath());
        return success? R.ok("success"): R.error("删除失败", "fail");
    }

    @GetMapping("/openapi")
    public Map<String, Object> getRouteOpenAPI(@RequestParam @Parameter(required = true) String path) {
        return afWorkerService.getOpenAPI(path);
    }

    @PostMapping("/refresh/one")
    @Operation(summary = "刷新一个 Route")
    public R<String> refreshOne() {
        throw new NotImplementedException();
    }

    @PostMapping("/refresh/all")
    @Operation(summary = "刷新所有 Route")
    public R<String> refreshAll() {
        throw new NotImplementedException();
    }

    @GetMapping("/access-log/by-path")
    public R<Object> getRouteAccessLogByPath(
            @NotBlank(message = "路由路径不允许为空") @Parameter(description = "路由路径", required = true) @RequestParam String routePath
    ) {
        throw new NotImplementedException();
    }

    @GetMapping("/access-log/by-rid")
    public R<Object> getRouteAccessLogByRid(
            @NotBlank(message = "路由 ID 不允许为空") @Parameter(description = "路由 ID", required = true) @RequestParam String rid
    ) {
        throw new NotImplementedException();
    }
}
