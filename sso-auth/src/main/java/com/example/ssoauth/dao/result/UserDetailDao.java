package com.example.ssoauth.dao.result;

import lombok.Data;

import java.util.List;

@Data
public class UserDetailDao {

    /**
     * 主键
     */
    private Long id;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码（经 md5 哈希）
     */
    private String password;

    /**
     * 用户昵称
     */
    private String screenName;

    /**
     *
     */
    private String role;

    private List<String> permissionList;
}
