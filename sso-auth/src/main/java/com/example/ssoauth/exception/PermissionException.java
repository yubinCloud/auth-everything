package com.example.ssoauth.exception;

public class PermissionException extends BaseBusinessException {

    public PermissionException() {
        super("权限不足");
    }
}
