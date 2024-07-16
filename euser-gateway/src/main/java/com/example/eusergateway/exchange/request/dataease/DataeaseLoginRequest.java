package com.example.eusergateway.exchange.request.dataease;

import lombok.Data;

@Data
public class DataeaseLoginRequest {

    private String username;

    private String password;

    private int loginType;

}
