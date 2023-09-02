package com.example.ssoauth.dao.param;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSelectCond {
    private String username;

    private String screenName;

    private String note;
}
