package com.example.gateway.exchange;

import com.example.gateway.exchange.request.DataeaseLoginRequest;
import com.example.gateway.exchange.response.DataeaseLoginResult;
import com.example.gateway.exchange.response.DataeaseResp;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;


@HttpExchange
public interface DataeaseBackendExchange {

    /**
     * Dataease backend 的登录
     * @param loginDto
     * @return
     */
    @PostExchange("/api/auth/login")
    Mono<DataeaseResp<DataeaseLoginResult>> login(@RequestBody DataeaseLoginRequest loginDto);
}
