package com.example.ssoauth.stp;

import cn.dev33.satoken.stp.StpInterface;
import cn.dev33.satoken.stp.StpUtil;
import com.example.ssoauth.constant.SaLoginConfExtraKey;
import com.example.ssoauth.exception.LoginException;
import com.example.ssoauth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final UserService userService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String username = (String) loginId;
        var userInDb =  userService.findByUsername(username);
        if (userInDb == null) {
            throw new LoginException();
        }
        return userInDb.getPermissionList();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String role = (String) StpUtil.getExtra(SaLoginConfExtraKey.ROLE);
        List<String> roleList = new ArrayList<>();
        roleList.add(role);
        return roleList;
    }
}
