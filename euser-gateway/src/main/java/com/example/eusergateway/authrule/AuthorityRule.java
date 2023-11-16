package com.example.eusergateway.authrule;

import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaRouterStaff;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.UriSpec;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public interface AuthorityRule {

    /**
     * 指定规则是否启用
     * @return
     */
    default boolean isEnabled() {
        return true;
    }

    /**
     * 获取 MATCH-PATH
     * @return
     */
    String getMatchPath();


    default List<String> excludes() {
        return Collections.emptyList();
    }

    SaRouterStaff authInfo();
}
