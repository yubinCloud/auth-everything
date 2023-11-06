package com.example.afencryptiongateway.function;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
 * 对请求进行解密
 */
@Slf4j
@RequiredArgsConstructor
public class RequestEncryptionFunction implements RewriteFunction<String, String> {

    private final ObjectMapper objectMapper;

    private final AskariExchange askariExchange;

    static private final String USERNAME_HEADER = "XUser";

    static private final String ENCRYPTION_MAGIC_NUMBER = "0521";

    @Override
    public Publisher<String> apply(ServerWebExchange exchange, String body) {
        String username = JwtUtil.parseUsername(exchange);
        FetchSecretKeyRequest fetchSecretKeyRequest = new FetchSecretKeyRequest();
        fetchSecretKeyRequest.setUsername(username);
        Mono<AskariResp<FetchSecretKeyResponse>> fetchResp = askariExchange.fetchSecretKey(fetchSecretKeyRequest);
        return fetchResp.handle((resp, sink) -> {
            String secretKey = resp.getData().getSecretKey();
//            String secretKey = "iQ3DuGokIVmH9qDGzdLl7Q==";
            byte[] keyBytes = Base64.decode(secretKey);
            AES aes = SecureUtil.aes(keyBytes);
//            System.out.println(aes.encryptHex(body));
            String decryptStr;
            log.info("request body: " + body);
            if (StrUtil.isBlank(body)) {
                decryptStr = "";
            } else {
                decryptStr = aes.decryptStr(body);
            }
            JSONObject jsonObject = JSONUtil.parseObj(decryptStr);
            try {
                sink.next(objectMapper.writeValueAsString(jsonObject));
            } catch (JsonProcessingException e) {
                sink.error(new ForbidRequestException("请求数据无法解析"));
            }
        });
    }

    public static void main(String[] args) {
        String secretKey = "0K+14t9dympNtjIxLYNbXQ==";
        byte[] keyBytes = Base64.decode(secretKey);
        AES aes = SecureUtil.aes(keyBytes);
        String body = "{ \"entname\":  \"威海丰达物流有限公司\"}";
        JSONObject jsonObject = JSONUtil.parseObj(body);
        String encryptHex = aes.encryptHex(body);
        System.out.println(jsonObject.toString());
        System.out.println(encryptHex);
    }
}
