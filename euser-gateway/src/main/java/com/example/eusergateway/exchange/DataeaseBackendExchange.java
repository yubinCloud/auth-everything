package com.example.eusergateway.exchange;

import com.example.eusergateway.exchange.request.dataease.DataeaseLoginRequest;
import com.example.eusergateway.exchange.response.dataease.DataeaseLoginResult;
import com.example.eusergateway.exchange.response.dataease.DataeaseResp;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@HttpExchange
public interface  DataeaseBackendExchange {

    /**
     * Dataease backend 登录
     * @param body
     * @return
     */
    @PostExchange("/api/auth/login")
    Mono<DataeaseResp<DataeaseLoginResult>> login(@RequestBody DataeaseLoginRequest body);

}
