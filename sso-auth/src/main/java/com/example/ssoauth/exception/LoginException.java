package com.example.ssoauth.exception;

public class LoginException extends BaseBusinessException {

    public LoginException() {
        super("登录错误，请检查用户名或密码");
    }
}
