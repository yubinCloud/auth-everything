package com.example.avuehelper.exception;

public class BzException extends RuntimeException {

    private int code;

    private Object data;

    public BzException(int code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public int getCode() {
        return code;
    }

}
