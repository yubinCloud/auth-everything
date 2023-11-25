package com.example.eusersso.exception;

/**
 * 远程调用出现报错
 */
public class RemoteCallException extends BaseBusinessException {

    public RemoteCallException(final String message) {
        super(message);
    }
}
