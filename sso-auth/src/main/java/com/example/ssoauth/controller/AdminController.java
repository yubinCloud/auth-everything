package com.example.ssoauth.controller;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.example.ssoauth.dao.param.UserSelectCond;
import com.example.ssoauth.dto.request.*;
import com.example.ssoauth.dto.response.PageResp;
import com.example.ssoauth.dto.response.R;
import com.example.ssoauth.entity.Role;
import com.example.ssoauth.entity.User;
import com.example.ssoauth.mapstruct.RoleConverter;
import com.example.ssoauth.service.RoleService;
import com.example.ssoauth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(
        name = "admin 级别的操作",
        description = "这些操作均被拦截，需要 SUPER-ADMIN 的权限"
)
public class AdminController {

    private final UserService userService;

    private final RoleService roleService;

    private final RoleConverter roleConverter;

    @PostMapping("/user/add")
    @Operation(summary = "添加用户")
    public R<String> addUser(
            @RequestBody @Valid NewUserDto userDto,
            @RequestHeader("User") String whoAmI
    ) {
        if (userDto.getJupyterhubAdmin() == null) {
            userDto.setJupyterhubAdmin(false);
        }
        userService.addUser(userDto, whoAmI);
        return R.ok("add success");
    }

    @PostMapping("/user/delete")
    @Operation(summary = "删除用户")
    public R<String> deleteUser(
            @RequestBody @Valid DeleteUserReq req,
            @RequestHeader("User") String whoAmI
    ) {
        userService.deleteByUsername(req.getUsername(), whoAmI);
        return R.ok("delete success");
    }

    @GetMapping("/user/list")
    @Operation(summary = "查看用户列表")
    public R<PageResp<User>> findUsers(
            @Min(1) @Parameter(description = "页码", example = "1") @RequestParam(required = false, defaultValue = "1") int pageNum,
            @Min(1) @Parameter(description = "页大小", example = "10") @RequestParam(required = false, defaultValue = "10") int pageSize,
            @Parameter(description = "过滤条件：用户名") @RequestParam(required = false) String username,
            @Parameter(description = "过滤条件：screen name") @RequestParam(required = false) String screenName,
            @Parameter(description = "过滤条件：description，支持模糊搜索") @RequestParam(required = false) String note,
            @Parameter(description = "手机号") @RequestParam(required = false) String mobile
    ) {
        username = prepostParam(username);
        screenName = prepostParam(screenName);
        note = prepostParam(note);
        var userSelectCond = new UserSelectCond(username, screenName, note, mobile);
        var pageInfo = userService.selectByPage(userSelectCond, pageNum, pageSize);

        PageResp<User> pageResp = new PageResp<>();
        List<User> userList = pageInfo.getList();
        pageResp.setPageNum(pageInfo.getPageNum());
        pageResp.setPageSize(pageInfo.getPageSize());
        pageResp.setTotal(pageInfo.getTotal());
        pageResp.setList(userList);

        return R.ok(pageResp);
    }

    @PostMapping("/user/update")
    @Operation(summary = "更新用户信息")
    public R<String> updateOneUser(
            @RequestBody @Valid UpdateUserReq updateReq,
            @RequestHeader("User") String whoAmI
    ) {
        userService.updateUserInfo(updateReq, whoAmI);
        return R.ok("update success");
    }

    @PostMapping("/permission/add")
    @Operation(summary = "perm | 增加用户权限")
    public R<String> addPermission(@RequestBody @Valid AddPermParam param) {
        userService.addPermission(param.getUsername(), param.getPermissionList());
        return R.ok("permission add success.");
    }

    @PostMapping("/permission/delete")
    @Operation(summary = "perm | 删除用户权限")
    public R<String> deletePermission(@RequestBody @Valid DeletePermParam param) {
        userService.deletePermission(param.getUsername(), param.getPermission());
        return R.ok("permission delete success");
    }

    @GetMapping("/role/list")
    @Operation(summary = "role | 查看所有角色")
    public R<List<Role>> getRoleList() {
        var roleList = roleService.findAll();
        return R.ok(roleList);
    }

    @GetMapping("/role/one/{id}")
    @Operation(summary = "role | 查看某一个角色的信息")
    public R<Role> getOneRole(
            @PathVariable("id") @NotNull @Valid int roleId
    ) {
        var role = roleService.findById(roleId);
        if (role == null) {
            return R.ok(null, "角色不存在");
        }
        return R.ok(role);
    }

    /**
     * WARNING: 需要注意修改 RoleLookupTable 数据与 DB 中数据的一致性
     */
    @PostMapping("/role/update")
    @Operation(summary = "role | 更改角色信息")
    public R<String> updateRole(
            @RequestBody @Valid UpdateRoleDto updateRoleDto
    ) {
        roleService.updateRole(updateRoleDto);
        return R.ok("update success");
    }

    @PostMapping("/role/add")
    @Operation(summary = "role | 增加角色")
    public R<String> addRole(
            @RequestBody @Valid NewRoleDto param
    ) {
        if (Objects.isNull(param.getPermissionList())) {
            param.setPermissionList(new ArrayList<>());
        }
        var roleDao = roleConverter.toRoleDao(param);
        int cnt = roleService.addRole(roleDao);
        if (cnt == 1) {
            return R.ok("add success");
        } else {
            return R.error("add failed", null);
        }
    }

    static final Set<Integer> FORBIDDEN_ROLES = new HashSet<>(List.of(1, 2, 3));

    @DeleteMapping("/role/delete/{id}")
    @Operation(summary = "role | 删除角色信息")
    public R<String> deleteRole(
            @PathVariable("id") @NotNull @Valid int roleId
    ) {
        if (FORBIDDEN_ROLES.contains(roleId)) {
            return R.error("该角色不允许被删除", null);
        }
        int result = roleService.deleteRole(roleId);
        if (result == 1) {
            return R.ok("delete success");
        } else {
            return R.error("delete fail", "delete fail");
        }
    }

    private String prepostParam(String selectParam) {
        if (StringUtils.isBlank(selectParam)) {
            selectParam = null;
        } else {
            selectParam = "%" + selectParam + "%";
        }
        return selectParam;
    }
}
