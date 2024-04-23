package com.example.eusersso.util;

import com.example.eusersso.feign.client.AuthFeignClient;
import com.example.eusersso.feign.response.UserInfo;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionCheckUtil {
    @Resource
    private AuthFeignClient authFeignClient;

    static private final String SUPER_ADMIN = "super-admin";

    public boolean superAdminCheck(String adminName , Integer tenantId){
        UserInfo userInfo = authFeignClient.userInfo(adminName, tenantId);
        List<String> roleList = userInfo.getRoleList().stream().filter(role -> role.equals(SUPER_ADMIN)).toList();
        //若为super-admin 返回 true , 否则 false
        return !roleList.isEmpty();
    }
}
