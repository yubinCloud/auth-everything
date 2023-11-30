package com.example.eusergateway.authrule;


import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;



@Component
public class EuserSSOAuthorityRule implements AuthorityRule {

    @Override
    public String getMatchPath() {
        return "/euser-sso/**";
    }


    @Override
    public SaRouterStaff authInfo() {
        return SaRouter.match("/euser-sso/**").notMatch("/euser-sso/auth/doLogin", "/euser-sso/sdt/sso/doLogin").free(r -> {
            SaRouter.match("/euser-sso/user/**", "/euser-sso/auth/**").check(rq -> StpUtil.checkLogin()).stop();
            SaRouter.match("/euser-sso/**", rq -> StpUtil.checkRole("it:FORBIDDEN")).stop();
        }).stop();
    }

}
