package com.example.ssoauth.service;

import ch.qos.logback.core.LogbackException;
import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import com.example.ssoauth.constant.SaLoginConfExtraKey;
import com.example.ssoauth.dto.response.LoginResp;
import com.example.ssoauth.exception.LoginException;
import com.example.ssoauth.mapstruct.UserConverter;
import com.example.ssoauth.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserService userService;

    private final UserConverter userConverter;

    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * @param username
     * @param pwd
     * @return
     */
    public LoginResp doLogin(String username, String pwd) {
        var userInDb = userService.findByUsername(username);  // 第一步：根据账号id，查询用户数据
        if (userInDb == null || !passwordEncoder.match(pwd, userInDb.getPassword())) {
            throw new LoginException();  // 用户名或密码校验错误
        }
        // 第二步：根据账号id，进行登录
        var loginConf = SaLoginConfig.setExtra(SaLoginConfExtraKey.ROLE, userInDb.getRole());  // 此处填入的参数应该保持用户表唯一，比如用户id，不可以直接填入整个 User 对象
        loginConf.setExtra(SaLoginConfExtraKey.PERMISSION, userInDb.getPermissionList());
        StpUtil.login(username, loginConf);
        // 构造 resp
        LoginResp resp = userConverter.toLoginResp(userInDb);
        resp.setToken(StpUtil.getTokenValue());
        return resp;
    }
}
