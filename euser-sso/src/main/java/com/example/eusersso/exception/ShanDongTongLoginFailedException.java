package com.example.eusersso.exception;

/**
 * 请求“山东通”的 SSO 时获取认证失败
 */
public class ShanDongTongLoginFailedException extends BaseBusinessException {
    public ShanDongTongLoginFailedException(final String message) {
        super(message);
    }
}
