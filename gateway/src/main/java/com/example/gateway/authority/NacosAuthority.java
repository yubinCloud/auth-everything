package com.example.gateway.authority;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;
import com.example.gateway.enums.PermissionEnum;
import org.springframework.stereotype.Component;

@Component
public class NacosAuthority implements Authority {

    private static final String MATCH_PATH = "/nacos";

    @Override
    public String getMatchPath() {
        return MATCH_PATH;
    }

    @Override
    public SaRouterStaff authInfo() {
        return SaRouter.match(MATCH_PATH + "/**", r -> StpUtil.checkPermission(PermissionEnum.NACOS));
    }
}
