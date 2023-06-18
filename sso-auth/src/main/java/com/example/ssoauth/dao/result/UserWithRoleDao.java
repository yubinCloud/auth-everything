package com.example.ssoauth.dao.result;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class UserWithRoleDao {
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

    private JSONArray permissionList;
}
