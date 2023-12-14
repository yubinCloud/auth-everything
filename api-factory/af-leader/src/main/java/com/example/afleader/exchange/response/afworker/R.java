package com.example.afleader.exchange.response.afworker;

import lombok.Data;

@Data
public class R<T> {

    private int code;

    private String message;

    private T data;
}
