package com.example.gateway.authority;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;
import com.example.gateway.enums.RoleEum;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AfAskariAuthority implements Authority {

    private static final String MATCH_PATH = "/af-askari";

    @Override
    public String getMatchPath() {
        return MATCH_PATH;
    }

    @Override
    public SaRouterStaff authInfo() {
        return SaRouter.match(MATCH_PATH + "/**").free(r -> {
            SaRouter.match(MATCH_PATH + "/private/**", rq -> StpUtil.checkRole(RoleEum.INTERNAL));  // 该接口不允许外部访问
        }).stop();
    }

    @Override
    public List<String> excludes() {
        return List.of(MATCH_PATH + "/auth/login");
    }
}
