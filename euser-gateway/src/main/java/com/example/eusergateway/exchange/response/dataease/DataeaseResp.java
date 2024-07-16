package com.example.eusergateway.exchange.response.dataease;

import lombok.Data;

@Data
public class DataeaseResp<T> {

    private boolean success;

    private String message;

    private T data;

}
