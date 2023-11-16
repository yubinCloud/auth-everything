package com.example.eusergateway.stp;

import cn.dev33.satoken.stp.StpInterface;
import com.example.eusergateway.service.EuserSsoService;
import com.example.eusergateway.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final EuserSsoService euserSsoService;

    private final PermissionService permissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String username = (String) loginId;
        List<String> permissionList = new ArrayList<>();
        var allPerm = euserSsoService.getPersonalPermissionInfo(username);
        // 处理 avue 的权限
        var avuePerm = allPerm.getAvuePermissions();
        if (Objects.nonNull(avuePerm)) {
            permissionList.addAll(permissionService.convertAvuePermissions(avuePerm));
        }
        // 处理 public-api 的权限
        var publicApiPerm = allPerm.getPublicApiPermissions();
        if (Objects.nonNull(publicApiPerm)) {
            permissionList.addAll(permissionService.convertPublicApiPermissions(publicApiPerm));
        }
        return permissionList;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return Collections.emptyList();
    }
}
