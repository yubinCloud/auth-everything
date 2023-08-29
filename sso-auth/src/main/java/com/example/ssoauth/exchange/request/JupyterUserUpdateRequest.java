package com.example.ssoauth.exchange.request;

import lombok.Data;

@Data
public class JupyterUserUpdateRequest {
    private String name;
    private Boolean admin;
}
