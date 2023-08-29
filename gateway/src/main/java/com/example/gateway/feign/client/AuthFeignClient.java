package com.example.gateway.feign.client;

import com.example.gateway.feign.client.response.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sso-auth")
public interface AuthFeignClient {

    @GetMapping("/auth/internal/user/info/{username}")
    UserInfo userInfo(@PathVariable String username);

}
