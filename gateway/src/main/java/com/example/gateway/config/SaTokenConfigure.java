package com.example.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.example.gateway.enums.RoleEum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SaTokenConfigure {
    @Bean
    public SaReactorFilter getSaReactorFilter() {
        return new SaReactorFilter()
                // 拦截地址
                .addInclude("/**")    /* 拦截全部path */
                // 开放地址
                .addExclude("/favicon.ico")
                .addExclude("/auth/v3/api-docs")  // API 文档相关信息
                .addExclude("/auth/swagger-ui/index.html")
                .addExclude("/auth/docs")
                .addExclude("/auth/doc.html")
                .addExclude("/nacos/**")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // ********** 登录校验：除登录接口外，均需要登录 ***********
                    SaRouter.match("/**", "/auth/login/doLogin", r -> StpUtil.checkLogin());
                    // ********** 权限认证：不同模块, 校验不同权限 ***************
                    authServiceRouter();    // auth 服务的校验
                    helloServiceRouter();   // hello-service 服务的校验
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> {
                    return SaResult.error(e.getMessage());
                })
                ;
    }

    private void authServiceRouter() {
        SaRouter.match("/auth/admin/**", r -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
    }

    private void helloServiceRouter() {
        SaRouter.match("/hello/admin", r -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
    }
}
