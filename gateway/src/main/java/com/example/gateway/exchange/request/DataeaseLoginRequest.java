package com.example.gateway.exchange.request;

import lombok.Data;

@Data
public class DataeaseLoginRequest {

    private String username;

    private String password;

    private int loginType;

}
