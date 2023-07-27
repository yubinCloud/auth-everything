package com.example.gateway.stp;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import cn.dev33.satoken.stp.StpInterface;
import com.example.gateway.feign.client.AuthFeignClient;
import com.example.gateway.feign.client.response.UserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SaTokenDaoRedisJackson redisJackson;

    private final AuthFeignClient authFeignClient;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    static private final String KEY_PREFIX_PERM = "aet:auth-perm:";

    static private final String KEY_PREFIX_ROLE = "aet:auth-role:";

    static private final long REDIS_TIMEOUT = 259200L;  // 3 天

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> permissionList;
        String username = (String) loginId;
        String keyInRedis = KEY_PREFIX_PERM + username;
        Object objInRedis = redisJackson.getObject(keyInRedis);
        if (objInRedis == null) {
            var future = executorService.submit(() -> authFeignClient.userInfo(username));  // 由于 Spring Cloud Gateway 是基于 webflux 的，因此需要封装为异步
            UserInfo userInfo = null;
            try {
                userInfo = future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("用户的权限信息获取失败");
            }
            permissionList = userInfo.getPermissionList();
            redisJackson.setObject(keyInRedis, permissionList, REDIS_TIMEOUT);
        } else {
            permissionList = (List<String>) objInRedis;
        }
        return permissionList;
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        List<String> roleList = new ArrayList<>();
        String username = (String) loginId;
        String keyInRedis = KEY_PREFIX_ROLE + username;
        Object objInRedis = redisJackson.getObject(keyInRedis);
        if (objInRedis == null) {
            var future = executorService.submit(() -> authFeignClient.userInfo(username));
            UserInfo userInfo = null;
            try {
                userInfo = future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("用户的角色信息获取失败");
            }
            String role = userInfo.getRole();
            roleList.add(role);
            redisJackson.setObject(keyInRedis, role, REDIS_TIMEOUT);
        } else {
            roleList.add((String) objInRedis);
        }
        return roleList;
    }
}
