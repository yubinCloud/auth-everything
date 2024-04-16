package com.example.ssoauth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.ssoauth.entity.User;
import com.example.ssoauth.service.JupyterService;
import com.example.ssoauth.service.UserService;
import com.example.ssoauth.util.LoginIdUtil;
import com.example.ssoauth.util.PasswordEncoder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    private final JupyterService jupyterService;

    private final PasswordEncoder passwordEncoder;

    private final LoginIdUtil loginIdUtil;

    @GetMapping("/user/info/{username}")
    @Operation(summary = "查看用户信息")
    public User userInfo(@PathVariable String username) {
        return userService.findByUsername(username);
    }

    @GetMapping("/user/pwd-hash")
    @Operation(summary = "获取用户密码的哈希值")
    public String hashPwd(
            @NotBlank @Parameter(description = "密码", required = true) @RequestParam String pwd
    ) {
        return passwordEncoder.encode(pwd);
    }

    @GetMapping("/user/jupyter/ctx")
    @Operation(summary = "获取用户的 jupyter 登录信息的上下文")
    public String getJupyterToken(
            @NotBlank @Parameter(description = "用户名", required = true) @RequestParam String username,
            @NotBlank @Parameter(description = "租户类型", required = true) @RequestParam Integer tenantId
    ) {
        String loginId = loginIdUtil.appendLoginId(tenantId,username);
        return jupyterService.findCtx(loginId);
    }

    @GetMapping("/user/jupyter/ctxByToken")
    @Operation(summary = "根据 token 获取用户的 jupyter ctx")
    public String getJupyterCtxByToken(
            @NotBlank @Parameter(description = "authz 的 token", required = true) @RequestParam String token
    ) {
        String loginId = (String) StpUtil.getLoginIdByToken(token);
        if (loginId == null) {
            return null;
        }
        return jupyterService.findCtx(loginId);
    }
}
