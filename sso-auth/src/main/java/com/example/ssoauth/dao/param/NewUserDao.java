package com.example.ssoauth.dao.param;

import lombok.Data;

@Data
public class NewUserDao {

    private Long id;

    private String username;

    private String password;

    private String screenName;

    private String roleList;

    private String permissionList;

    private String note;

    private String mobile;
}
