package com.xxl.job.admin.feign.response;

import java.util.List;

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
    private String role;

    /**
     * 权限列表
     */
    private List<String> permissionList;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getPermissionList() {
        return permissionList;
    }

    public void setPermissionList(List<String> permissionList) {
        this.permissionList = permissionList;
    }

}

