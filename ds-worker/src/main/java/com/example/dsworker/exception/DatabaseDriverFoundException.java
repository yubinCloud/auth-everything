package com.example.dsworker.exception;

/**
 * 数据库驱动未找到
 */
public class DatabaseDriverFoundException extends RuntimeException {

    public DatabaseDriverFoundException(final String message) {
        super(message);
    }
}
