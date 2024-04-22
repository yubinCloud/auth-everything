package com.example.eusersso.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.example.eusersso.converter.EuserConverter;
import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dto.response.LoginResp;
import com.example.eusersso.exception.LoginException;
import com.example.eusersso.mapper.EuserMapper;
import com.example.eusersso.util.PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final EuserMapper euserMapper;

    private final PasswordEncoder passwordEncoder;

    private final EuserConverter euserConverter;

    private static final Integer DEFAULT_TENANT_ID = 1;

    @Transactional
    public LoginResp doLogin(Integer tenantId, String username, String password) {
        if (tenantId == null){
            tenantId = DEFAULT_TENANT_ID;
        }
        // 查询用户数据
        var userInDb = euserMapper.selectByUsernameAndTenantId(username, tenantId);
        if (Objects.isNull(userInDb) || !passwordEncoder.match(password, userInDb.getPassword())) {
            throw new LoginException(LoginException.CANNOT_MATCH);
        }
        if (!userInDb.isChecked()) {
            throw new LoginException(LoginException.ACCOUNT_UNCHECKED);
        }
        return loginEuser(userInDb);
    }

    public LoginResp loginEuser(EuserDao euserDao) {
        // 登录
        StpUtil.login(euserDao.getUsername());
        // 构造 resp
        LoginResp resp = euserConverter.toLoginResp(euserDao);
        resp.setToken(StpUtil.getTokenValue());
        return resp;
    }
}
