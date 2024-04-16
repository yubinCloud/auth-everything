package com.example.ssoauth.service;

import cn.dev33.satoken.stp.SaLoginConfig;
import cn.dev33.satoken.stp.StpUtil;
import com.example.ssoauth.constant.SaLoginConfExtraKey;
import com.example.ssoauth.dto.response.LoginResp;
import com.example.ssoauth.exception.LoginException;
import com.example.ssoauth.mapstruct.UserConverter;
import com.example.ssoauth.mapstructutil.UserConverterUtil;
import com.example.ssoauth.util.LoginIdUtil;
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
    private final LoginIdUtil loginIdUtil;
    private static final Integer DEFAULT_TENANT_ID = 1;

    /**
     * 用户登录
     *
     * @param username
     * @param pwd
     * @param tenantId
     * @return
     */
    @Transactional
    public LoginResp doLogin(String username, String pwd, Integer tenantId) {
        //若未选择租户类型,默认为1
        if (tenantId == null) {
            tenantId = DEFAULT_TENANT_ID;
        }
        // 1. 根据账号id，查询用户数据并校验
        var userInDb = userService.findByUsernameAndTenantId(username, tenantId);
        if (userInDb == null || !passwordEncoder.match(pwd, userInDb.getPassword())) {
            throw new LoginException();  // 用户名或密码校验错误
        }
        // 2. 根据账号id，进行登录
        String loginId = loginIdUtil.appendLoginId(tenantId, username);
        StpUtil.login(loginId);
        // 3. 登录 jupyter
        jupyterService.loginJupyter(loginId);
        // 构造 resp
        LoginResp resp = userConverter.toLoginResp(userInDb);
        resp.setToken(StpUtil.getTokenValue());
        return resp;
    }
}
