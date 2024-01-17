package com.example.dsworker.exception;

public class SQLExecuteException extends RuntimeException {

    public SQLExecuteException(final String message) {
        super(message);
    }
}
