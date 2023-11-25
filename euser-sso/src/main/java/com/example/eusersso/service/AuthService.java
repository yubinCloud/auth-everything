package com.example.eusersso.service;

import cn.dev33.satoken.stp.StpUtil;
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

    @Transactional
    public LoginResp doLogin(String username, String password) {
        // 查询用户数据
        var userInDb = euserMapper.selectByUsername(username);
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
