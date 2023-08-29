package com.example.gateway.controller;

import com.example.gateway.dto.response.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * 断路器的 fallback 处理端点
 * 触发断路时由此来处理
 */
@RestController
public class FallbackController {

    @RequestMapping("/fallback")
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public Mono<R<Object>> fallbackHandler() {
        return Mono.just(R.unavailable("Service unable to reach."));
    }
}
