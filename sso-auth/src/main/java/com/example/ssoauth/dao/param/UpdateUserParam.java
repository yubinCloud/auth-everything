package com.example.ssoauth.dao.param;

import lombok.Data;

@Data
public class UpdateUserParam {

    private String username;

    private String password;

    private String screenName;

    private Integer role;

    private String note;
}
