package com.example.ssoauth.controller;

import com.example.ssoauth.dao.result.UserDetailDao;
import com.example.ssoauth.dto.response.LoginResp;
import com.example.ssoauth.dto.response.R;
import com.example.ssoauth.dto.response.UserInfoResp;
import com.example.ssoauth.mapstruct.UserConverter;
import com.example.ssoauth.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "微服务内部接口",
        description = "该接口不对外公开，包含获取用户信息等接口"
)
public class InternalController {

    private final UserService userService;

    private final UserConverter userConverter;

    @GetMapping("/user/info/{username}")
    @Operation(summary = "查看用户信息")
    public UserInfoResp userInfo(@PathVariable String username) {
        UserDetailDao userDetail = userService.findByUsername(username);
        return userConverter.toUserInfoResp(userDetail);
    }
}
