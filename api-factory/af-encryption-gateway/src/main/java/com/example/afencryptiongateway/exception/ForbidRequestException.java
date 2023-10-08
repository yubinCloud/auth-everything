package com.example.afencryptiongateway.exception;

public class ForbidRequestException extends RuntimeException {
    public ForbidRequestException(String msg) {
        super(msg);
    }
}
