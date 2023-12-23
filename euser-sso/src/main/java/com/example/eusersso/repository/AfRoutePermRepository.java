package com.example.eusersso.repository;

import cn.hutool.json.JSONUtil;
import com.example.eusersso.entity.Euser;
import com.example.eusersso.mapper.EuserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        this.euserMapper = euserMapper;
    }

    /**
     * 设置用户可访问的 public-api 列表
     * @param username
     * @param routes
     */
    public void setEuserRoutes(String username, List<String> routes) {
        redisTemplate.multi();  // mini-transaction
        final String key = KEY_PREFIX + username;
        redisTemplate.delete(key);
        redisTemplate.opsForSet().add(key, routes.toArray());
        redisTemplate.expire(key, Duration.ofSeconds(EXPIRE_TIME));
        redisTemplate.exec();
    }
    // TODO

    public List<String> getPermList(String username) {
        String permListInDb = euserMapper.queryAfRoutePerms(username);
        if (Objects.isNull(permListInDb)) {
            return Collections.emptyList();
        }
        var permList = JSONUtil.parseArray(permListInDb).toList(String.class);
        final String key = KEY_PREFIX + username;
        redisTemplate.multi();
        redisTemplate.delete(key);
        redisTemplate.opsForSet().add(key, permList.toArray());
        redisTemplate.expire(key, Duration.ofSeconds(EXPIRE_TIME));
        redisTemplate.exec();
        return permList;
    }

    public boolean checkPerm(String username, String routePath) {
        final String key = KEY_PREFIX + username;
        redisTemplate.multi();
        redisTemplate.opsForSet().isMember(key, routePath);

    }
}
