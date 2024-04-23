package com.example.eusersso.exception;

public class PermissionDeniedException extends BaseBusinessException{
    public PermissionDeniedException(final String message) {
        super(message);
    }
    public static final String INSUFFICIENT_PRIVILEGES = "权限不足,只能选择当前管理员所属租户类型";
}
