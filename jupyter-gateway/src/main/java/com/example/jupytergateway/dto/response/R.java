package com.example.jupytergateway.dto.response;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class R<T> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static final int CODE_SUCCESS = 0;
    public static final int CODE_ERROR = -500;
    public static final int BAD_REQUEST = -400;

    @Getter
    @Setter
    private int code;

    @Getter
    @Setter
    private String msg;

    @Getter
    @Setter
    private T data;

    public static R<Object> ok() {
        return new R<>(CODE_SUCCESS, "ok", null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(CODE_SUCCESS, "ok", data);
    }

    public static <T> R<T> ok(T data, String msg) {
        return new R<>(CODE_SUCCESS, msg, data);
    }

    public static R<Object> error() {
        return new R<>(CODE_ERROR, "error", null);
    }

    public static R<Object> error(String msg) {
        return new R<>(CODE_ERROR, msg, null);
    }

    public static R<Object> badRequest(String msg) {
        return new R<>(BAD_REQUEST, msg, null);
    }

    public String toString() {
        return "{\"code\": " + this.getCode() + ", \"msg\": " + this.transValue(this.getMsg()) + ", \"data\": " + this.transValue(this.getData()) + "}";
    }

    private String transValue(Object value) {
        return value instanceof String ? "\"" + value + "\"" : String.valueOf(value);
    }
}