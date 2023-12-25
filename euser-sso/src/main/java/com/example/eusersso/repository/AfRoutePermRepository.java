package com.example.eusersso.repository;

import cn.hutool.json.JSONUtil;
import com.example.eusersso.dto.request.UpdateAddPublicAPIPermissionRequest;
import com.example.eusersso.entity.Euser;
import com.example.eusersso.mapper.EuserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理 api-factory 模块中可访问 API 的权限信息
 */
@Component
public class AfRoutePermRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private final EuserMapper euserMapper;

    private static final long EXPIRE_TIME = 86400;  // 24小时

    private static final String KEY_PREFIX = "afu";

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
     * 同时会重置缓存
     * @param username
     * @return
     */
    public List<String> getPermList(String username) {
        String permListInDb = euserMapper.queryAfRoutePerms(username);
        if (Objects.isNull(permListInDb)) {
            return Collections.emptyList();
        }
        var permList = JSONUtil.parseArray(permListInDb).toList(String.class);
        permList = permList.parallelStream().collect(Collectors.toSet()).stream().toList();
        final String key = KEY_PREFIX + username;
        redisTemplate.multi();
        redisTemplate.delete(key);
        redisTemplate.opsForSet().add(key, permList.toArray());
        redisTemplate.expire(key, Duration.ofSeconds(EXPIRE_TIME));
        redisTemplate.exec();
        return permList;
    }

    public void addPermission(String username, List<String> routes) {
        euserMapper.appendPublicAPI(username, routes);
        clearCache(username);
    }

    public void deletePermission(String username, String apiId) {
        euserMapper.deletePublicAPI(username, apiId);
        clearCache(username);
    }

    public void clearCache(String username) {
        redisTemplate.delete(KEY_PREFIX + username);
    }
}
