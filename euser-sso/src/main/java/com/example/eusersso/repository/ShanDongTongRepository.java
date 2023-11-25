package com.example.eusersso.repository;

import com.example.eusersso.config.ShanDongTongProperties;
import com.example.eusersso.exception.RemoteCallException;
import com.example.eusersso.exchange.ShanDongTongExchange;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.MemcachedClient;
import org.springframework.stereotype.Repository;

import java.util.Objects;


/**
 * 对接山东通平台
 * 用于从山东通平台获取 access-token
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class ShanDongTongRepository {

    private final MemcachedClient memcachedClient;

    private final ShanDongTongExchange shanDongTongExchange;

    private final ShanDongTongProperties shanDongTongProperties;

    /**
     * access-token 在 memcached 中的 key
     */
    public static final String SDT_ACCESS_TOKEN_KEY_IN_MEMCACHED = "sdt:at";  // ShanDongTong AccessToken

    @Resource
    private Cache<String, String> shanDongTongAccessTokenCache;

    @PostConstruct
    public void init() {
        if (shanDongTongProperties.getEnabled()) {
            getAccessToken();
        }
    }

    public String getAccessToken() {
        // 如果不开启山东通，则直接返回 null
        if (Objects.isNull(shanDongTongProperties.getEnabled()) || !shanDongTongProperties.getEnabled()) {
            return null;
        }
        // 尝试走本地缓存
        String accessToken = shanDongTongAccessTokenCache.getIfPresent(SDT_ACCESS_TOKEN_KEY_IN_MEMCACHED);
        if (Objects.nonNull(accessToken)) {
            return accessToken;
        }
        // 尝试走 memcached 缓存和远程调用
        Object objInCached = memcachedClient.get(SDT_ACCESS_TOKEN_KEY_IN_MEMCACHED);
        if (Objects.isNull(objInCached)) {
            var tokenResp = shanDongTongExchange.getAccessToken(shanDongTongProperties.getCorpId(), shanDongTongProperties.getSecret());
            Object tokenInResp = tokenResp.get("access_token");
            if (Objects.isNull(tokenInResp)) {
                throw new RemoteCallException("山东通平台调用出错，response：" + tokenResp);
            }
            accessToken = (String) tokenInResp;
        } else {
            accessToken = (String) objInCached;
        }
        shanDongTongAccessTokenCache.put(SDT_ACCESS_TOKEN_KEY_IN_MEMCACHED, accessToken);
        return accessToken;
    }
}
