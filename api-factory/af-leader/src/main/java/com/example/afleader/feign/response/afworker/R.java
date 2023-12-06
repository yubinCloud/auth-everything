package com.example.afleader.feign.response.afworker;

import lombok.Data;

@Data
public class R<T> {

    private int code;

    private String message;

    private T data;
}
