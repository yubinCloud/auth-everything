package com.example.eusergateway.service;

import com.example.eusergateway.exchange.DataeaseBackendExchange;
import com.example.eusergateway.exchange.request.dataease.DataeaseLoginRequest;
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
        loginReq.setPassword("xxx");
        loginReq.setLoginType(3);
        return dataeaseBackendExchange.login(loginReq).mapNotNull(resp -> {
            if (!resp.isSuccess())
                return null;
            return resp.getData().getToken();
        });
    }

}
