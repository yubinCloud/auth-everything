package com.example.ssoauth.exchange.response;

import lombok.Data;

/**
 * JupyterResponse（JR）
 * jupyter-service 的统一 Response
 * @param <T>
 */
@Data
public class JR<T> {

    public static final int SUCCESS = 0;

    private Integer code;
    private T data;
}
