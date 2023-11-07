package com.example.eusersso.exception;

/**
 * 所有业务异常的基类
 */
public class BaseBusinessException extends RuntimeException {

    public BaseBusinessException(final String message) {
        super(message);
    }
}
