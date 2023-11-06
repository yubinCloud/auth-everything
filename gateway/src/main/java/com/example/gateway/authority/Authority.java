package com.example.gateway.authority;

import cn.dev33.satoken.router.SaRouterStaff;

import java.util.Collections;
import java.util.List;

public interface Authority {

    public default boolean isEnabled() {
        return true;
    }

    public String getMatchPath();

    /**
     * 返回相关的权限信息
     * @return
     */
    public SaRouterStaff authInfo();

    public default List<String> excludes() {
        return Collections.emptyList();
    }
}
