package com.example.ssoauth.exchange;

import com.example.ssoauth.exchange.request.JupyterUserUpdateRequest;
import com.example.ssoauth.exchange.request.JupyterUsrCreateRequest;
import com.example.ssoauth.exchange.response.JR;
import com.example.ssoauth.exchange.response.JupyterLoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.*;

/**
 * 与 Jupyter 的 API 进行交互
 */
@HttpExchange
public interface JupyterExchange {

    public static final String AUTHORIZATION = "Authorization";

    /**
     * 添加用户
     */
    @PostExchange("/hub/dadp/user/create/{username}")
    JR<String> createUser(
            @PathVariable("username") String username,
            @RequestBody JupyterUsrCreateRequest body,
            @RequestHeader(AUTHORIZATION) String authorization
    );

    /**
     * 删除用户
     * @param username
     * @param authorization
     * @return
     */
    @DeleteExchange("/hub/dadp/user/del/{username}")
    JR<String> deleteUser(
            @PathVariable("username") String username,
            @RequestHeader(AUTHORIZATION) String authorization
    );

    /**
     * 更新用户信息
     * @param username
     * @param body
     * @param authorization
     * @return
     */
    @PatchExchange("/hub/dadp/user/update/{username}")
    JR<String> updateUser(
            @PathVariable("username") String username,
            @RequestBody JupyterUserUpdateRequest body,
            @RequestHeader(AUTHORIZATION) String authorization
    );

    /**
     * 登录 jupyter，获取 jupyter 的 token
     */
    @PostExchange("/hub/dadp/user/login")
    ResponseEntity<JR<JupyterLoginResponse>> jupyterLogin(@RequestHeader("User") String username);
}
