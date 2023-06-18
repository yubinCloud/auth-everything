package com.example.ssoauth.dao.result;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import cn.hutool.json.JSONArray;
import lombok.Data;

/**
 * 用户信息表
 * @TableName user
 */
@Data
public class UserDao {
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
    private Integer role;


    private JSONArray permissionList;
}