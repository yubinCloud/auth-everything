package com.example.jupytergateway.service;

import com.example.jupytergateway.dao.RedisDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JupyterService {

    private final RedisDao redisDao;

    static private final String KEY_PREFIX_JUPYTER = "aet:j-ctx:";

    public String findToken(String username) {
        String keyInRedis = redisKeyFactory(username);
        return redisDao.get(keyInRedis);
    }

    private String redisKeyFactory(String username) {
        return KEY_PREFIX_JUPYTER + username;
    }
}
