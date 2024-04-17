package com.example.ssoauth.service;

import cn.dev33.satoken.dao.SaTokenDaoRedisJackson;
import com.example.ssoauth.dao.result.JupyterContext;
import com.example.ssoauth.exception.BaseBusinessException;
import com.example.ssoauth.exchange.JupyterExchange;
import com.example.ssoauth.exchange.response.JR;
import com.example.ssoauth.util.LoginIdUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JupyterService {

    private final JupyterExchange jupyterExchange;

    private final SaTokenDaoRedisJackson redisJackson;

    private final LoginIdUtil loginIdUtil;

    static private final String KEY_PREFIX_JUPYTER = "aet:j-ctx:";

    public void loginJupyter(String loginId) {
        //获取 loginId 中的 username , 登录 jupyter
        String[] loginIdEntity = loginIdUtil.splitLoginId(loginId);
        String username = loginIdEntity[1];

        var loginResp = jupyterExchange.jupyterLogin(username);  // 远程调用 jupyter 的登录接口
        // 解析 response 获取 token
        var respBody = loginResp.getBody();
        if (respBody == null || respBody.getCode() != JR.SUCCESS) {
            throw new BaseBusinessException("Jupyter 登录失败");
        }
        String token = respBody.getData().getToken();
        // 将 token 存入 redis
        String keyInRedis = redisKeyFactory(loginId);
        long REDIS_TIMEOUT = 2592000;
        redisJackson.set(keyInRedis, token, REDIS_TIMEOUT);
    }

    public String findCtx(String loginId) {

        String keyInRedis = redisKeyFactory(loginId);
        String token = redisJackson.get(keyInRedis);
        if (token == null) {
            return null;
        } else {
            return token;
        }
    }

    private String redisKeyFactory(String loginId) {
        return KEY_PREFIX_JUPYTER + loginId;
    }
}
