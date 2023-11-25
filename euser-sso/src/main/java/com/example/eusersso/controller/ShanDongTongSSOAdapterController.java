package com.example.eusersso.controller;

import com.example.eusersso.dto.request.shandongtong.sso.SdtLoginParam;
import com.example.eusersso.dto.response.LoginResp;
import com.example.eusersso.dto.response.R;
import com.example.eusersso.exception.LoginException;
import com.example.eusersso.service.AuthService;
import com.example.eusersso.service.EuserService;
import com.example.eusersso.service.ShanDongTongService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/sdt/sso")
@RequiredArgsConstructor
@Validated
@Tag(
        name = "山东通对接接口"
)
public class ShanDongTongSSOAdapterController {

    private final ShanDongTongService shanDongTongService;

    private final EuserService euserService;

    private final AuthService authService;

    @PostMapping("/doLogin")
    @Operation(summary = "经过山东通的 SSO 来登录")
    public R<LoginResp> doLogin(@RequestBody @Valid SdtLoginParam param) {
        var userInSdt = shanDongTongService.authFromShanDongTong(param.getCode());
        var euser = euserService.selectByMobile(userInSdt.getMobile());
        if (Objects.isNull(euser)) {
            throw new LoginException("未找到匹配的用户");
        }
        LoginResp loginResp = authService.loginEuser(euser);
        return R.ok(loginResp);
    }
}
