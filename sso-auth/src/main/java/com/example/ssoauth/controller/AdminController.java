package com.example.ssoauth.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.example.ssoauth.dao.param.UserSelectCond;
import com.example.ssoauth.dto.request.*;
import com.example.ssoauth.dto.response.PageResp;
import com.example.ssoauth.dto.response.R;
import com.example.ssoauth.dto.response.UserInfoResp;
import com.example.ssoauth.enums.RoleEnum;
import com.example.ssoauth.mapstruct.UserConverter;
import com.example.ssoauth.service.UserService;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "admin 级别的操作",
        description = "这些操作均被拦截，需要 SUPER-ADMIN 的权限"
)
public class AdminController {

    private final UserService userService;

    private final UserConverter userConverter;

    @PostMapping("/user/add")
    @SaCheckRole(RoleEnum.SUPER_ADMIN)
    @Operation(summary = "添加用户")
    public R<String> addUser(@RequestBody @Valid NewUserDto userDto) {
        userService.addUser(userDto);
        return R.ok("add success");
    }

    @PostMapping("/user/delete")
    @SaCheckRole(RoleEnum.SUPER_ADMIN)
    @Operation(summary = "删除用户")
    public R<String> deleteUser(@RequestBody @Valid DeleteUserReq req) {
        userService.deleteByUsername(req.getUsername());
        return R.ok("delete success");
    }

    @GetMapping("/user/list")
    @SaCheckRole(RoleEnum.SUPER_ADMIN)
    @Operation(summary = "查看用户列表")
    public R<PageResp<UserInfoResp>> findUsers(
            @Min(1) @Parameter(description = "页码", example = "1") @RequestParam(required = false, defaultValue = "1") int pageNum,
            @Min(1) @Parameter(description = "页大小", example = "10") @RequestParam(required = false, defaultValue = "10") int pageSize,
            @Parameter(description = "过滤条件：用户名") @RequestParam(required = false) String username,
            @Parameter(description = "过滤条件：screen name") @RequestParam(required = false) String screenName,
            @Parameter(description = "过滤条件：role") @RequestParam(required = false) String role
    ) {
        var userSelectCond = new UserSelectCond(username, screenName, role);
        var pageInfo = userService.selectByPage(userSelectCond, pageNum, pageSize);

        PageResp<UserInfoResp> pageResp = new PageResp<>();
        List<UserInfoResp> userList = pageInfo.getList().stream().map(userConverter::toUserInfoResp).toList();
        pageResp.setPageNum(pageInfo.getPageNum());
        pageResp.setPageSize(pageInfo.getPageSize());
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(userList);

        return R.ok(pageResp);
    }

    @PostMapping("/user/update")
    @SaCheckRole(RoleEnum.SUPER_ADMIN)
    @Operation(summary = "更新用户信息")
    public R<String> updateOneUser(@RequestBody @Valid UpdateUserReq updateReq) {
        userService.updateUserInfo(updateReq);
        return R.ok("update success");
    }

    @PostMapping("/permission/add")
    @SaCheckRole(RoleEnum.SUPER_ADMIN)
    @Operation(summary = "增加权限")
    public R<String> addPermission(@RequestBody @Valid AddPermParam param) {
        userService.addPermission(param.getUsername(), param.getPermissionList());
        return R.ok("permission add success.");
    }

    @PostMapping("/permission/delete")
    @SaCheckRole(RoleEnum.SUPER_ADMIN)
    @Operation(summary = "删除权限")
    public R<String> deletePermission(@RequestBody @Valid DeletePermParam param) {
        userService.deletePermission(param.getUsername(), param.getPermission());
        return R.ok("permission delete success");
    }
}
