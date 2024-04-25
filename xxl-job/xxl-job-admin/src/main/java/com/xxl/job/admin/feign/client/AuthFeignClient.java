package com.xxl.job.admin.feign.client;

import com.xxl.job.admin.feign.response.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "sso-auth")
public interface AuthFeignClient {
    @GetMapping("/auth/internal/user/info/{username}")
    User userInfo(@PathVariable String username);


    @GetMapping("/user/pwd-hash")
    String hashPwd( @RequestParam String pwd);
}
