package com.example.eusersso.controller;

import com.example.eusersso.dao.param.AvueRoleSelectCond;
import com.example.eusersso.dto.request.NewAvueRoleDto;
import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.PageResp;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.entity.AvueRole;
import com.example.eusersso.service.EuserForAvueService;
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

import java.util.Objects;

@RestController
@RequestMapping("/admin/avue")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "【avue】用户信息的管理"
)
public class AdminForAvueController {

    private final EuserForAvueService euserService;

    @PostMapping("/user/create")
    @Operation(summary = "创建用户")
    public R<String> createUser(
            @RequestBody @Valid NewUserDto userDto,
            @RequestHeader(ConstantUtil.IUSER_WHOAMI_HEADER) String whoAmI
    ) {
        int result = euserService.createEuser(userDto, whoAmI);
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
            @Parameter(description = "过滤条件：role id") @RequestParam(required = false) Integer roleId
    ) {
        var page = euserService.selectPageByCond(username, screenName, roleId, pageNum, pageSize);
        return R.ok(page);
    }

    @PostMapping("/role/create")
    @Operation(summary = "创建角色")
    public R<AvueRole> createRole(@RequestBody @Valid NewAvueRoleDto newAvueRoleDto) {
        AvueRole role = euserService.createRole(newAvueRoleDto);
        if (role.getRoleId() == 0) {
            return R.error("创建失败", null);
        }
        return R.ok(role);
    }

    @PostMapping("/role/update")
    @Operation(summary = "更新角色信息")
    public R<String> updateRole(@RequestBody @Valid AvueRole update) {
        int result = euserService.updateRole(update);
        if (result == 0) {
            return R.error("更新失败", null);
        }
        return R.ok("update success");
    }

    @GetMapping("/role/list")
    @Operation(summary = "查看角色列表")
    public R<PageResp<AvueRole>> getRoleList(
            @Min(1) @Parameter(description = "页码", example = "1") @RequestParam(required = false, defaultValue = "1") int pageNum,
            @Min(1) @Parameter(description = "页大小", example = "10") @RequestParam(required = false, defaultValue = "10") int pageSize,
            @Parameter(description = "过滤条件：role id") @RequestParam(required = false) Integer roleId,
            @Parameter(description = "过滤条件：角色名，支持模糊搜索") @RequestParam(required = false) String roleName
    ) {
        var cond = new AvueRoleSelectCond();
        cond.setRoleId(roleId);
        if (!Objects.isNull(roleName)) {
            cond.setName("%" + roleName + "%");
        }
        var page = euserService.selectAvueRolePageByCond(cond, pageNum, pageSize);
        var pageResp = PageResp.of(page);
        return R.ok(pageResp);
    }

    @DeleteMapping("/role/{roleId}")
    @Operation(summary = "删除角色")
    public R<String> deleteRole(@PathVariable("roleId") @Min(0) int roleId) {
        int deleteResult = euserService.deleteRole(roleId);
        if (deleteResult == 0) {
            return R.error("删除失败", null);
        }
        return R.ok("success");
    }
}
