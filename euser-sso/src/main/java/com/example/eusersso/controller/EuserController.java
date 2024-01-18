package com.example.eusersso.controller;

import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dto.response.PersonalPermissionInfo;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.entity.AvuePermission;
import com.example.eusersso.entity.SysHome;
import com.example.eusersso.service.EuserService;
import com.example.eusersso.util.ConstantUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "用户个人信息的接口",
        description = "用户个人调用"
)
public class EuserController {

    private final EuserService euserService;

    @GetMapping("/permission/all")
    @Operation(summary = "获取当前用户的所有权限信息")
    public R<PersonalPermissionInfo> getPersonalAllPermissions(
            @RequestHeader(ConstantUtil.EUSER_WHOAMI_HEADER) String username
    ) {
        EuserDao euser = euserService.selectOneSimple(username);
        PersonalPermissionInfo permissionInfo = new PersonalPermissionInfo();
        permissionInfo.setAvuePermissions(euserService.getAvuePermission(euser.getAvueRoles()));
        return R.ok(permissionInfo);
    }

    @GetMapping("/permission/avue")
    @Operation(summary = "获取当前用户的 avue 权限信息")
    public R<List<AvuePermission>> getPersonalAvuePermission(
            @RequestHeader(ConstantUtil.EUSER_WHOAMI_HEADER) String username
    ) {
        List<AvuePermission> permissions = euserService.getAvuePermission(username);
        return R.ok(permissions);
    }

    @GetMapping("/sys-homes/all")
    @Operation(summary = "查看本用户所能看到的系统首页")
    public R<List<SysHome>> getSysHomes(@RequestHeader(ConstantUtil.EUSER_WHOAMI_HEADER) String username) {
        var sysHomes = euserService.getSysHomes(username);
        return R.ok(sysHomes);
    }
}
