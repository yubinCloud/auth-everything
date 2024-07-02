package com.example.gateway.service;

import com.github.benmanes.caffeine.cache.Cache;
import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class JupyterService {

    @Resource
    private SaTokenDaoRedisJackson redisJackson;

    @Resource
    private Cache<String, String> jupyterTokenCache;

    static private final String KEY_PREFIX_JUPYTER = "aet:j-ctx:";

    public String findToken(String username) {
        // 通过 Caffeine + Redis 双缓存来获取 user 的 jupyter token
        String token = jupyterTokenCache.getIfPresent(username);
        if (token == null) {
            token = redisJackson.get(KEY_PREFIX_JUPYTER + username);
            jupyterTokenCache.put(username, token);
        }
        return token;
    }
}
