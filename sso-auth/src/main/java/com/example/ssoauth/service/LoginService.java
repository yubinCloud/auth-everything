package com.example.ssoauth.service;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import com.example.ssoauth.constant.SaLoginConfExtraKey;
import com.example.ssoauth.dto.response.LoginResp;
import com.example.ssoauth.exception.LoginException;
import com.example.ssoauth.mapstruct.UserConverter;
import com.example.ssoauth.util.PasswordEncoder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserService userService;

    private final UserConverter userConverter;

    private final PasswordEncoder passwordEncoder;

    private final JupyterService jupyterService;

    /**
     * 用户登录
     * @param username
     * @param pwd
     * @return
     */
    @Transactional
    public LoginResp doLogin(String username, String pwd, HttpServletResponse response) {
        // 1. 根据账号id，查询用户数据并校验
        var userInDb = userService.findByUsername(username);
        if (userInDb == null || !passwordEncoder.match(pwd, userInDb.getPassword())) {
            throw new LoginException();  // 用户名或密码校验错误
        }
        // 2. 根据账号id，进行登录
        String role = userInDb.getRole();
        var loginConf = SaLoginConfig.setExtra(SaLoginConfExtraKey.ROLE, role);  // 此处填入的参数应该保持用户表唯一，比如用户id，不可以直接填入整个 User 对象
        StpUtil.login(username, loginConf);
        // 3. 登录 jupyter
        var cookies = jupyterService.loginJupyter(username);
        for (String cookie: cookies) {
            response.addHeader(HttpHeaders.SET_COOKIE, cookie);
        }
        // 构造 resp
        LoginResp resp = userConverter.toLoginResp(userInDb);
        resp.setToken(StpUtil.getTokenValue());
        return resp;
    }
}
