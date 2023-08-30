package com.example.gateway.config;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.example.gateway.authority.Authority;
import com.example.gateway.dto.response.R;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SaTokenConfigure {

    private final List<Authority> authorities;

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
                .addExclude("/actuator/**")
                .addExclude("/xxl-job-admin/static/**")  // 开放 xxl-job 的静态资源
                .addExclude("/xxl-job-admin/assets/**")  // 开放 xxl-job 的静态资源
                .addExclude("/xxl-job-admin/joblog/logDetailPage/**")  // 开放 xxl-job 的查看日志页面
                .addExclude("/xxl-job-admin/joblog/logDetailCat/**")   // 开放 xxl-job 的查看日志页面
                .addExclude("/avue/oss/**")  // 开放 avue 的所有静态资源
                .addExclude("/jupyter/**")
                .addExclude("/ws/**")
                .addExclude("/ds-worker/**")
                .addExclude("/avue/**")
                // 鉴权方法：每次访问进入
                .setAuth(obj -> {
                    // ********** 登录校验：除登录接口外，均需要登录 ***********
                    SaRouter.match("/**", "/auth/login/doLogin", r -> StpUtil.checkLogin());
                    // *****************************************************

                    for (var authority: authorities) {
                        authority.authInfo();
                    }
                }).setError(e -> {
                    if (e instanceof SaTokenException) {
                        return handleSaTokenException((SaTokenException) e);
                    } else {
                        return R.error(e.getMessage());
                    }
                });
    }

    private R<Object> handleSaTokenException(SaTokenException e) {
        switch (e.getCode()) {
            case 11011 -> {
                return R.forbidden("未能读取到有效Token");
            }
            case 11012, 30201, 30202 -> {
                return R.forbidden("Token 无效");
            }
            case 11013, 30204 -> {
                return R.forbidden("Token 已过期");
            }
            case 11051 -> {
                return R.forbidden("当前账号不符合权限要求");
            }
            case 11041 -> {
                return R.forbidden("当前账号不符合角色要求");
            }
            default -> {
                return R.forbidden("网关安全校验错误，内部错误码：" + e.getCode());
            }
        }
    }
}
