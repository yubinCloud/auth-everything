package com.example.gateway.authority;

import cn.dev33.satoken.router.SaRouterStaff;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AfLeaderAuthority implements Authority {

    private static final String MATCH_PATH = "/af-leader";

    @Override
    public String getMatchPath() {
        return MATCH_PATH;
    }

    @Override
    public SaRouterStaff authInfo() {
        return null;
    }

    @Override
    public List<String> excludes() {
        return List.of(MATCH_PATH + "/static/**");
    }
}
