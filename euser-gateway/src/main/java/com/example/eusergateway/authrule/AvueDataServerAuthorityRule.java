package com.example.eusergateway.authrule;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;
import com.example.eusergateway.service.PermissionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AvueDataServerAuthorityRule implements AuthorityRule {

    private final PermissionFactory permissionFactory;

    @Override
    public String getMatchPath() {
        return "/avue/**";
    }

    @Override
    public SaRouterStaff authInfo() {
        return SaRouter.match("/avue/**").free(r -> {
            // 大屏页面，检查用户是否具有该大屏 view-id 的权限
            SaRouter.match("/avue/visual/detail", rq -> {
                String visualId = SaHolder.getRequest().getParam("id", null);
                if (visualId != null) {
                    StpUtil.checkPermission(permissionFactory.createVisualIdPermission(Long.parseLong(visualId)));
                }
            });
        });
    }
}
