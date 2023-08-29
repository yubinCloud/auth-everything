package com.example.jupytergateway.service;

import com.example.jupytergateway.exchange.AuthExchange;
import com.example.jupytergateway.exchange.response.JupyterContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final AuthExchange authExchange;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    /**
     * 通过调用 auth-service 获取 jupyter context
     * @param username
     * @return
     */
    public String findJupyterContext(String username) {
        var future = executorService.submit(() -> authExchange.getJupyterContext(username));
        String token = null;
        try {
            token = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("获取 jupyter 登录信息失败，错误：" + e.getMessage());
        }
        return token;
    }

    public String findJupyterContextByToken(String authzToken) {
        var future = executorService.submit(() -> authExchange.getJupyterContextByToken(authzToken));
        String token = null;
        try {
            token = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("获取 jupyter 登录信息失败，错误：" + e.getMessage());
        }
        return token;
    }
}
