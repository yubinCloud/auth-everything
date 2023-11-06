package com.example.afencryptiongateway.exchange.response;

import lombok.Data;

@Data
public class AskariResp<T> {
    private int code;
    private String message;
    private T data;
}
