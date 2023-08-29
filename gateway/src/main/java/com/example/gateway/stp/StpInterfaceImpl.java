package com.example.gateway.stp;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.stp.StpInterface;
import com.example.gateway.feign.client.AuthFeignClient;
import com.example.gateway.feign.client.response.UserInfo;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private Cache<String, String> roleCache;

    @Resource
    private Cache<String, List<String>> permissionCache;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    static private final String KEY_PREFIX_PERM = "aet:auth-perm:";

    static private final String KEY_PREFIX_ROLE = "aet:auth-role:";

    @Value("${sa-token.timeout}")
    private Long REDIS_TIMEOUT = 259200L;  // 3 天

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPermissionList(Object loginId, String loginType) {
        String username = (String) loginId;
        List<String> permissionList = permissionCache.getIfPresent(username);
        if (permissionList != null) {
            return permissionList;
        }
        String keyInRedis = KEY_PREFIX_PERM + username;
        Object objInRedis = redisJackson.getObject(keyInRedis);
        if (objInRedis == null) {
            var userInfo = getUserInfo(username);
            permissionList = userInfo.getPermissionList();
            redisJackson.setObject(keyInRedis, permissionList, REDIS_TIMEOUT);
        } else {
            permissionList = (List<String>) objInRedis;
        }
        permissionCache.put(username, permissionList);
        return permissionList;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roleList = new ArrayList<>();
        String username = (String) loginId;
        String role = roleCache.getIfPresent(username);
        if (role != null) {
            roleList.add(role);
            return roleList;
        }
        String keyInRedis = KEY_PREFIX_ROLE + username;
        role = redisJackson.get(keyInRedis);
        if (role == null) {
            var userInfo = getUserInfo(username);
            role = userInfo.getRole();
            roleList.add(role);
            redisJackson.set(keyInRedis, role, REDIS_TIMEOUT);
        } else {
            roleList.add(role);
        }
        roleCache.put(username, role);
        return roleList;
    }

    /**
     * 通过远程调用 sso-auth 服务获取 user-info
     * @param username
     * @return
     */
    private UserInfo getUserInfo(String username) {
        var future = executorService.submit(() -> authFeignClient.userInfo(username));
        UserInfo userInfo = null;
        try {
            userInfo = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("用户信息获取失败");
        }
        return userInfo;
    }
}
