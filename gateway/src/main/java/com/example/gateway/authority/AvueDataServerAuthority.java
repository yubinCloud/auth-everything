package com.example.gateway.authority;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

@Component
public class AvueDataServerAuthority implements Authority {

    private static final String MATCH_PATH = "/avue";

    @Override
    public String getMatchPath() {
        return MATCH_PATH;
    }

    @Override
    public SaRouterStaff authInfo() {
        return SaRouter.match(MATCH_PATH + "/**").free(r -> {
            // 检查 view-id
            SaRouter.match(MATCH_PATH + "/visual/detail", rq -> {
                String viewId = SaHolder.getRequest().getParam("id", null);
                if (viewId != null) {
                    StpUtil.checkPermission("avue:" + viewId);
                }
            });
        }).stop();
    }
}
