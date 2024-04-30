package com.example.eusersso.controller;

import com.example.eusersso.dto.request.EuserUappDto;
import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.PageResp;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.service.EuserForUappService;
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
@RequestMapping("/admin/uapp")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "【uapp】用户信息的管理"
)
public class AdminForUappController {

    private final EuserForUappService euserService;

    @PostMapping("/user/create")
    @Operation(summary = "创建用户")
    public R<String> createUser(
            @RequestBody @Valid NewUserDto userDto,
            @RequestHeader(ConstantUtil.IUSER_WHOAMI_HEADER) String whoAmI,
            @RequestHeader(ConstantUtil.TENANT_ID_HEADER) Integer tenantId
    ) {
        int result = euserService.createEuser(userDto, whoAmI, tenantId);
        if (result >= 1) {
            return R.ok("success");
        }
        return new R<>(R.CODE_ERROR, "插入失败，请稍后尝试", "fail");
    }

    @GetMapping("/user/list")
    @Operation(summary = "查看用户列表")
    public R<PageResp<EuserListItem>> userPage(
            @Min(1) @Parameter(description = "页码", example = "1") @RequestParam(required = false, defaultValue = "1") int pageNum,
            @Min(1) @Parameter(description = "页大小", example = "10") @RequestParam(required = false, defaultValue = "10") int pageSize,
            @Parameter(description = "过滤条件：用户名，支持模糊搜索") @RequestParam(required = false) String username,
            @Parameter(description = "过滤条件：screen name，支持模糊搜索") @RequestParam(required = false) String screenName,
            @Parameter(description = "过滤条件：uappRole") @RequestParam(required = false) Integer uappRole,
            @RequestHeader(ConstantUtil.TENANT_ID_HEADER) Integer tenantId
    ) {
        var page = euserService.selectPageByCond(username, screenName, uappRole, tenantId, pageNum, pageSize);
        return R.ok(page);
    }

    @PostMapping("/uapp/add")
    @Operation(summary = "为uapp外部用户新增一个uapp")
    public R<String> addUapp(@RequestBody @Valid EuserUappDto uappDto,
                             @RequestHeader(ConstantUtil.IUSER_WHOAMI_HEADER) String whoAmI,
                             @RequestHeader(ConstantUtil.TENANT_ID_HEADER) Integer tenantId
    ) {

        int result = euserService.addUapp(uappDto, whoAmI, tenantId);
        if (result >= 1) {
            return R.ok("success");
        }
        return new R<>(R.CODE_ERROR, "添加uapp失败，请稍后尝试", "fail");
    }

    @PostMapping("/uapp/update")
    @Operation(summary = "修改uapp外部用户的uapp")
    public R<String> updateUapp(@RequestBody @Valid EuserUappDto uappDto,
                                @RequestHeader(ConstantUtil.IUSER_WHOAMI_HEADER) String whoAmI,
                                @RequestHeader(ConstantUtil.TENANT_ID_HEADER) Integer tenantId
    ) {

        int result = euserService.addUapp(uappDto, whoAmI, tenantId);
        if (result >= 1) {
            return R.ok("success");
        }
        return new R<>(R.CODE_ERROR, "添加uapp失败，请稍后尝试", "fail");
    }
}
