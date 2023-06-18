package com.example.ssoauth.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class LoginResp {

    private String username;
    private String screenName;
    private String role;
    private List<String> permissionList;
    private String token;
}
