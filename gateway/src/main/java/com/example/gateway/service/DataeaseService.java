package com.example.gateway.service;

import com.example.gateway.exchange.DataeaseBackendExchange;
import com.example.gateway.exchange.request.DataeaseLoginRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataeaseService {

    private final DataeaseBackendExchange dataeaseBackendExchange;

    public Mono<String> fetchAuthToken(String username) {
        var loginReq = new DataeaseLoginRequest();
        loginReq.setUsername(username);
        loginReq.setPassword("xxx");  // 无需密码
        loginReq.setLoginType(3);
        return dataeaseBackendExchange.login(loginReq).mapNotNull(resp -> {
            if (!resp.isSuccess())
                return null;
            return resp.getData().getToken();
        });
    }
}
