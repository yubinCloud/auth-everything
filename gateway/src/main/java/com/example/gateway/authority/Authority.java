package com.example.gateway.authority;

import cn.dev33.satoken.router.SaRouterStaff;

public interface Authority {

    public String getMatchPath();

    /**
     * 返回相关的权限信息
     * @return
     */
    public SaRouterStaff authInfo();
}
