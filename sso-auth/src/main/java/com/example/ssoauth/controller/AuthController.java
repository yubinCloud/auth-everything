package com.example.ssoauth.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.StpUtil;
import com.example.ssoauth.dto.request.LoginParam;
import com.example.ssoauth.dto.response.LoginResp;
import com.example.ssoauth.dto.response.R;
import com.example.ssoauth.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "认证接口",
        description = "包括登录、登出等操作"
)
public class AuthController {

    private final LoginService loginService;

    @PostMapping("/doLogin")
    @Operation(summary = "登录（POST 方式）")
    public R<LoginResp> postDoLogin(
            @RequestBody @Valid LoginParam param,
            HttpServletResponse response
    ) {
        LoginResp resp =  loginService.doLogin(param.getUsername(), param.getPwd(), response);
        return R.ok(resp);
    }

    @GetMapping("/doLogin")
    @Operation(summary = "登录（GET 方式）")
    public R<LoginResp> getDoLogin(
            @NotBlank @Parameter(description = "用户名", required = true) @RequestParam String username,
            @NotBlank @Parameter(description = "密码", required = true) @RequestParam String pwd,
            HttpServletResponse response
    ) {
        LoginResp resp = loginService.doLogin(username, pwd, response);
        return R.ok(resp);
    }

    @GetMapping("/isLogin")
    @Operation(summary = "查询当前登录状态")
    public R<String> isLogin() {
        // StpUtil.isLogin() 查询当前客户端是否登录，返回 true 或 false
        boolean isLogin = StpUtil.isLogin();
        return R.ok("当前客户端是否登录：" + isLogin);
    }

    @GetMapping("/checkLogin")
    @Operation(summary = "校验当前登录状态")
    public R<String> checkLogin() {
        // 检验当前会话是否已经登录, 如果未登录，则抛出异常：`NotLoginException`
        StpUtil.checkLogin();
        // 抛出异常后，代码将走入全局异常处理（GlobalException.java），如果没有抛出异常，则代表通过了登录校验，返回下面信息
        return R.ok("校验登录成功，这行字符串是只有登录后才会返回的信息");
    }

    @PostMapping("/doLogout")
    @SaCheckLogin
    @Operation(summary = "登出")
    public R<String> logout() {
        StpUtil.logout();
        return R.ok("退出登录");
    }
}
