package com.example.eusersso.controller;

import com.example.eusersso.dto.request.LoginParam;
import com.example.eusersso.dto.response.LoginResp;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "认证接口",
        description = "包括登录、登出等操作"
)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/doLogin")
    @Operation(summary = "登录（POST 方式）")
    public R<LoginResp> postDoLogin(
            @RequestBody @Valid LoginParam param
    ) {
        var loginResp = authService.doLogin(param.getUsername(), param.getPassword());
        return R.ok(loginResp);
    }

    @GetMapping("/doLogin")
    @Operation(summary = "登录（GET 方式）")
    public R<LoginResp> getDoLogin(
            @NotBlank @Parameter(description = "用户名", required = true) @RequestParam String username,
            @NotBlank @Parameter(description = "密码", required = true) @RequestParam String pwd
    ) {
        LoginResp resp = authService.doLogin(username, pwd);
        return R.ok(resp);
    }


}
