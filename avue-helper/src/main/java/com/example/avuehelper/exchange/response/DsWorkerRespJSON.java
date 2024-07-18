package com.example.avuehelper.exchange.response;

import lombok.Data;

@Data
public class DsWorkerRespJSON<T> {
    private int code;

    private String msg;

    private T data;
}
