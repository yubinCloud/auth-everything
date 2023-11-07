package com.example.eusersso.exception;

public class LoginException extends BaseBusinessException {

    public LoginException(final String message) {
        super(message);
    }

    public static final String CANNOT_MATCH = "登录失败，请检查用户名或密码";

    public static final String ACCOUNT_UNCHECKED = "该账号暂不可用，请联系系统管理员";
}
