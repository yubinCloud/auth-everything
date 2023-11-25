package com.example.ssoauth.entity;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String screenName;
    private List<String> roleList;
    private List<String> permissionList;
    private String note;
    private Long createTime;
    private String mobile;
}
