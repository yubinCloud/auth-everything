package com.example.gateway.authority;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;
import com.example.gateway.enums.PermissionEnum;
import com.example.gateway.enums.RoleEum;
import org.springframework.stereotype.Component;

@Component
public class SentinelAuthority implements Authority {

    private static final String MATCH_PATH = "/sentinel";

    @Override
    public String getMatchPath() {
        return MATCH_PATH;
    }

    @Override
    public SaRouterStaff authInfo() {
        return SaRouter.match(MATCH_PATH + "/**").free(r -> {
            SaRouter.match(MATCH_PATH + "/**", rq -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
            SaRouter.match(MATCH_PATH + "/**", rq -> StpUtil.checkRole(PermissionEnum.SENTINEL));
        }).stop();
    }
}
