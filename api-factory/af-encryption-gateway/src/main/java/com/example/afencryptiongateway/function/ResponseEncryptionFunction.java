package com.example.afencryptiongateway.function;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import com.example.afencryptiongateway.exception.ForbidRequestException;
import com.example.afencryptiongateway.exchange.AskariExchange;
import com.example.afencryptiongateway.exchange.request.FetchSecretKeyRequest;
import com.example.afencryptiongateway.exchange.response.AskariResp;
import com.example.afencryptiongateway.exchange.response.FetchSecretKeyResponse;
import com.example.afencryptiongateway.util.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Base64;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * 对响应做解密
 */
@Slf4j
@RequiredArgsConstructor
public class ResponseEncryptionFunction implements RewriteFunction<String, String> {

    private final ObjectMapper objectMapper;

    private final AskariExchange askariExchange;

    static private final String USERNAME_HEADER = "XUser";

    static private final String ENCRYPTION_MAGIC_NUMBER = "0521";

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String body) {
        String username = JwtUtil.parseUsername(exchange);
        if (username == null) {
            throw new ForbidRequestException("请求未携带 token");
        }
        FetchSecretKeyRequest fetchSecretKeyRequest = new FetchSecretKeyRequest();
        fetchSecretKeyRequest.setUsername(username);
        Mono<AskariResp<FetchSecretKeyResponse>> fetchResp = askariExchange.fetchSecretKey(fetchSecretKeyRequest);
        return fetchResp.handle((resp, sink) -> {
            Map<String, Object> respJSON = new HashMap<>();
            String secretKey = resp.getData().getSecretKey();
            byte[] keyBytes = Base64.decode(secretKey);
            AES aes = SecureUtil.aes(keyBytes);
            String encryptHex = aes.encryptHex(body);  // 加密为16进制表示
            String magic = aes.encryptHex(ENCRYPTION_MAGIC_NUMBER);  // 魔数，用于校验解密结果
            respJSON.put("data", encryptHex);
            respJSON.put("magic", magic);
            try {
                sink.next(objectMapper.writeValueAsString(respJSON));
            } catch (JsonProcessingException e) {
                sink.error(new ForbidRequestException("响应数据无法解析"));
            }
        });
    }
}
