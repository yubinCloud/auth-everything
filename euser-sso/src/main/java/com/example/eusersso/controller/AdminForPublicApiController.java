package com.example.eusersso.controller;

import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.PageResp;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.service.EuserForPublicApiService;
import com.example.eusersso.util.ConstantUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/public-api")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "【public-api】用户信息的管理"
)
public class AdminForPublicApiController {

    private final EuserForPublicApiService euserService;

    @PostMapping("/user/create")
    @Operation(summary = "创建用户")
    public R<String> createUser(
            @RequestBody @Valid NewUserDto userDto,
            @RequestHeader(ConstantUtil.IUSER_WHOAMI_HEADER) String whoAmI
    ) {
        int result = euserService.createEuser(userDto, whoAmI);
        if (result == 0) {
            return R.error("插入失败，请稍后尝试", null);
        }
        return R.ok("success");
    }

    @GetMapping("/user/list")
    @Operation(summary = "查看用户列表")
    public R<PageResp<EuserListItem>> userPage(
            @Min(1) @Parameter(description = "页码", example = "1") @RequestParam(required = false, defaultValue = "1") int pageNum,
            @Min(1) @Parameter(description = "页大小", example = "10") @RequestParam(required = false, defaultValue = "10") int pageSize,
            @Parameter(description = "过滤条件：用户名，支持模糊搜索") @RequestParam(required = false) String username,
            @Parameter(description = "过滤条件：screen name，支持模糊搜索") @RequestParam(required = false) String screenName,
            @Parameter(description = "过滤条件：role id") @RequestParam(required = false) Integer roleId
    ) {
        var page = euserService.selectPageByCond(username, screenName, roleId, pageNum, pageSize);
        return R.ok(page);
    }
}
