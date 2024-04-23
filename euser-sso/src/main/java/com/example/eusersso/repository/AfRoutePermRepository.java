package com.example.eusersso.repository;

import cn.hutool.json.JSONUtil;
import com.example.eusersso.mapper.EuserMapper;
import com.example.eusersso.util.LoginIdUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理 api-factory 模块中可访问 API 的权限信息
 */
@Repository
public class AfRoutePermRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private final EuserMapper euserMapper;

    @Resource
    private LoginIdUtil loginIdUtil;

    private static final long EXPIRE_TIME = 86400;  // 24小时

    private static final String KEY_PREFIX = "afu:";

    @Autowired
    public AfRoutePermRepository(RedisConnectionFactory afRedisConnectionFactory, EuserMapper euserMapper) {
        redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(afRedisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        this.euserMapper = euserMapper;
    }

    /**
     * 从 DB 中获取用户的 API 权限列表
     *
     * @param username
     * @param tenantId
     * @return
     */
    public List<String> queryPermListInDB(String username, Integer tenantId) {
        String permListInDb = euserMapper.queryAfRoutePerms(username, tenantId);
        if (Objects.isNull(permListInDb)) {
            return Collections.emptyList();
        }
        var permList = JSONUtil.parseArray(permListInDb).toList(String.class);
        permList = permList.parallelStream().collect(Collectors.toSet()).stream().toList();  // 去重
        return permList;
    }

    @Transactional
    public List<String> queryCheckedPermList(String username) {

        final String key = KEY_PREFIX + username;
        var hasKey = redisTemplate.hasKey(key);
        Set<Object> cachedValue = Boolean.TRUE.equals(hasKey) ? redisTemplate.opsForSet().members(key) : null;
        if (Objects.isNull(cachedValue)) {
            String apiIs = euserMapper.queryCheckedByUsernameInPublicAPI(username);
            var permList = JSONUtil.parseArray(apiIs).toList(String.class);
            permList = permList.parallelStream().collect(Collectors.toSet()).stream().toList();  // 去重
            if (!permList.isEmpty()) {
                redisTemplate.opsForSet().add(key, permList.toArray());
                redisTemplate.expire(key, Duration.ofSeconds(EXPIRE_TIME));
            }
            return permList;
        }
        List<String> permList = new ArrayList<>();
        cachedValue.forEach(v -> permList.add((String) v));
        return permList;
    }

    public void addPermission(String username, Integer tenantId, List<String> routes) {
        euserMapper.appendPublicAPI(username, tenantId, routes);
        String loginId = loginIdUtil.appendLoginId(tenantId, username);
        clearCache(loginId);
    }

    public void deletePermission(String username, Integer tenantId, String apiId) {
        euserMapper.deletePublicAPI(username, tenantId, apiId);
        String loginId = loginIdUtil.appendLoginId(tenantId, username);
        clearCache(loginId);
    }

    public void clearCache(String loginId) {
        redisTemplate.delete(KEY_PREFIX + loginId);
    }
}
