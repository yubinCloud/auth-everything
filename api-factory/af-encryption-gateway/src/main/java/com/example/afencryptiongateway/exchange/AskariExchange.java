package com.example.afencryptiongateway.exchange;

import com.example.afencryptiongateway.exchange.request.FetchSecretKeyRequest;
import com.example.afencryptiongateway.exchange.response.AskariResp;
import com.example.afencryptiongateway.exchange.response.FetchSecretKeyResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

@HttpExchange
public interface AskariExchange {

    /**
     * 获取 secret key
     */
    @PostExchange("/apisix/encryption/secret-key")
    Mono<AskariResp<FetchSecretKeyResponse>> fetchSecretKey(@RequestBody FetchSecretKeyRequest body);

}
