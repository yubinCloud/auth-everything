package com.example.gateway.feign.client.response;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
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
    private List<String> roleList;

    /**
     * 权限列表
     */
    private List<String> permissionList;

    private String note;

    private Long createTime;
}
