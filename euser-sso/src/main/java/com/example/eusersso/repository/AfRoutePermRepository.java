package com.example.eusersso.repository;

import cn.hutool.json.JSONUtil;
import com.example.eusersso.mapper.EuserMapper;
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
     * @param username
     * @return
     */
    public List<String> queryPermListInDB(String username) {
        String permListInDb = euserMapper.queryAfRoutePerms(username);
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
        Set<Object> cachedValue = Boolean.TRUE.equals(hasKey)? redisTemplate.opsForSet().members(key): null;
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
