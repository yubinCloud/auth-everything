package com.example.eusersso.feign.client;


import com.example.eusersso.feign.response.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sso-auth")
public interface AuthFeignClient {

    @GetMapping("/auth/internal/user/info/{username}/{tenantId}")
    UserInfo userInfo(@PathVariable String username, @PathVariable Integer tenantId);

}
