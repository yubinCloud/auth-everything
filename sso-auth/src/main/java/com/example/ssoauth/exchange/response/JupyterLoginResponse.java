package com.example.ssoauth.exchange.response;

import lombok.Data;

@Data
public class JupyterLoginResponse {
    private String username;
    private String token;
}
