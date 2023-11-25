package com.example.eusergateway.authrule;

import cn.dev33.satoken.router.SaRouterStaff;

import java.util.Collections;
import java.util.List;

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
