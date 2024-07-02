package com.example.gateway.exchange.response;

import lombok.Data;

@Data
public class DataeaseResp<T> {

    private boolean success;

    private String message;

    private T data;

}
