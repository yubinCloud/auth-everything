package com.example.eusergateway.authrule;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

@Component
public class GenReportAuthorityRule implements AuthorityRule {

    @Override
    public String getMatchPath() {
        return "/genreport/**";
    }

    // genreports 服务只允许开放个别服务
    @Override
    public SaRouterStaff authInfo() {
        return SaRouter.match("/genreports/**")
                .notMatch("/genreports/tplconfig", "/genreports/hasreport", "/genreports/editapi", "/genreports/genreport")
                .check(rq -> StpUtil.checkRole("it:FORBIDDEN"));
    }
}
