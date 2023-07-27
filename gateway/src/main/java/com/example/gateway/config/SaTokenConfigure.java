package com.example.gateway.config;

import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;
import com.example.gateway.constant.PermissionEnum;
import com.example.gateway.enums.RoleEum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

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
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // ********** 登录校验：除登录接口外，均需要登录 ***********
                    SaRouter.match("/**", "/auth/login/doLogin", r -> StpUtil.checkLogin());
                    // *****************************************************

                    // ********** 权限认证服务 ***************
                    SaRouter.match("/auth/**").free(r -> {
                        SaRouter.match("/auth/admin/**", rq -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
                        SaRouter.match("/auth/internal/**", rq -> StpUtil.checkRole(RoleEum.INTERNAL));
                    }).stop();
                    // **************************************

                    // ********** sentinel 服务 ***************
                    SaRouter.match("/sentinel/**", r -> StpUtil.checkRole(RoleEum.SUPER_ADMIN)).stop();
                    // ***************************************

                    // ********** actuator ********************
                    SaRouter.match("/actuator/**").free(r -> {
                        SaRouter.match("/actuator/**", rq -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
                        SaRouter.match("/actuator/**", rq -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
                    }).stop();
                    // ****************************************

                    // ********** hello service 服务 **************
                    SaRouter.match("/hello/**").free(r -> {
                        SaRouter.match("/hello/admin", rq -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
                        SaRouter.match("/hello/docs", rq -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
                        SaRouter.match("/hello/redoc", rq -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
                        SaRouter.match("/hello/openapi.json", rq -> StpUtil.checkRole(RoleEum.SUPER_ADMIN));
                    }).stop();
                    // ********************************************
                })
                // 异常处理方法：每次setAuth函数出现异常时进入
                .setError(e -> SaResult.error(e.getMessage()));
    }
}
