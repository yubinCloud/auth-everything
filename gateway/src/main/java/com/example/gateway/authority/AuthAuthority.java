package com.example.gateway.authority;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;
import com.example.gateway.enums.RoleEum;
import org.springframework.stereotype.Component;

@Component
public class AuthAuthority implements Authority {

    private static final String MATCH_PATH = "/auth";

    @Override
    public String getMatchPath() {
        return MATCH_PATH;
    }

    @Override
    public SaRouterStaff authInfo() {
        return SaRouter.match(MATCH_PATH + "/**").free(r -> {
            SaRouter.match(MATCH_PATH + "/admin/**", rq -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
            SaRouter.match(MATCH_PATH + "/internal/**", rq -> StpUtil.checkRoleOr(RoleEum.SUPER_ADMIN, RoleEum.INTERNAL));
        }).stop();
    }
}
