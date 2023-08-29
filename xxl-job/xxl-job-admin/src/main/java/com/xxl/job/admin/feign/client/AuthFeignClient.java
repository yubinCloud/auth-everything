package com.xxl.job.admin.feign.client;

import com.xxl.job.admin.feign.response.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "sso-auth")
public interface AuthFeignClient {

    @GetMapping("/auth/internal/user/info/{username}")
    UserInfo userInfo(@PathVariable("username") String username);

    @GetMapping("/auth/internal/user/pwd-hash")
    String hashPwd(@RequestParam("pwd") String pwd);
}
