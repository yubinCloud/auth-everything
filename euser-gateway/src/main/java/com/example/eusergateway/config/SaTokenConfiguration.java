package com.example.eusergateway.config;

import cn.dev33.satoken.exception.SaTokenException;
import cn.dev33.satoken.reactor.filter.SaReactorFilter;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import com.example.eusergateway.authrule.AuthorityRule;
import com.example.eusergateway.dto.response.R;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SaTokenConfiguration {

    private final List<AuthorityRule> authorityRules;

    @Bean
    public SaReactorFilter getSaReactorFilter() {
        var filter = new SaReactorFilter()
                .addInclude("/**")
                .addExclude("/actuator/health")
                .setAuth(obj -> {
                    // ********** 登录校验：除登录接口外，均需要登录 ***********
                    SaRouter.match("/**", "/euser-sso/auth/doLogin", r -> StpUtil.checkLogin());

                    authorityRules.forEach(rule -> {
                        if (rule.isEnabled()) {
                            rule.authInfo();
                        }
                    });
                })
                .setError(e -> e instanceof SaTokenException? handleSaTokenException((SaTokenException) e): R.error(e.getMessage()));
        // 添加每个 exclude
        authorityRules.forEach(rule -> {
            if (rule.isEnabled()) {
                rule.excludes().forEach(filter::addExclude);
            }
        });
        return filter;
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
