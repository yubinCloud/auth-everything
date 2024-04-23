package com.example.gateway.stp;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.stp.StpInterface;
import com.example.gateway.feign.client.AuthFeignClient;
import com.example.gateway.feign.client.response.UserInfo;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class StpInterfaceImpl implements StpInterface {

    @Resource
    private SaTokenDaoRedisJackson redisJackson;

    @Resource
    private AuthFeignClient authFeignClient;

    @Resource
    private Cache<String, List<String>> roleCache;

    @Resource
    private Cache<String, List<String>> permissionCache;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    static private final String KEY_PREFIX_PERM = "aet:auth-perm:";

    static private final String KEY_PREFIX_ROLE = "aet:auth-role:";

    private static final Long REDIS_TIMEOUT = 259200L;  // 3 天

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPermissionList(Object loginId, String loginType) {
        //解析loginId,分为tenantId和username
        String loginIdString = (String) loginId;
        String[] loginIdArr = loginIdString.split(",");
        Integer tenantId = Integer.parseInt(loginIdArr[0]);
        String username = loginIdArr[1];

        List<String> permissionList = permissionCache.getIfPresent(loginIdString);
        if (permissionList != null) {
            return permissionList;
        }
        String keyInRedis = KEY_PREFIX_PERM + loginIdString;
        Object objInRedis = redisJackson.getObject(keyInRedis);
        if (objInRedis == null) {
            var userInfo = getUserInfo(username,tenantId);
            permissionList = userInfo.getPermissionList();
            redisJackson.setObject(keyInRedis, permissionList, REDIS_TIMEOUT);
        } else {
            permissionList = (List<String>) objInRedis;
        }
        permissionCache.put(loginIdString, permissionList);
        return permissionList;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<String> getRoleList(Object loginId, String loginType) {
        //解析loginId,分为tenantId和username
        String loginIdString = (String) loginId;
        String[] loginIdArr = loginIdString.split(",");
        Integer tenantId = Integer.parseInt(loginIdArr[0]);
        String username = loginIdArr[1];

        List<String> roleList = roleCache.getIfPresent(loginIdString);
        if (roleList != null) {
            return roleList;
        }
        String keyInRedis = KEY_PREFIX_ROLE + loginIdString;
        Object objInRedis = redisJackson.getObject(keyInRedis);
        if (objInRedis == null) {
            var userInfo = getUserInfo(username,tenantId);
            roleList = userInfo.getRoleList();
            redisJackson.setObject(keyInRedis, roleList, REDIS_TIMEOUT);
        } else {
            roleList = (List<String>) objInRedis;
        }
        roleCache.put(loginIdString, roleList);
        return roleList;
    }

    /**
     * 通过远程调用 sso-auth 服务获取 user-info
     * @param username
     * @param tenantId
     * @return
     */
    private UserInfo getUserInfo(String username,Integer tenantId) {
        var future = executorService.submit(() -> authFeignClient.userInfo(username,tenantId));
        UserInfo userInfo = null;
        try {
            userInfo = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("用户信息获取失败");
        }
        return userInfo;
    }
}
