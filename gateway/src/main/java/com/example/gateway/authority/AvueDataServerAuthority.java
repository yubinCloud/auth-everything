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
            // 大屏页面，检查用户是否具有该大屏 view-id 的权限
            SaRouter.match(MATCH_PATH + "/visual/detail", rq -> {
                String viewId = SaHolder.getRequest().getParam("id", null);
                if (viewId != null) {
                    StpUtil.checkPermission("avue:vs:" + viewId);
                }
            });
        }).stop();
    }
}
