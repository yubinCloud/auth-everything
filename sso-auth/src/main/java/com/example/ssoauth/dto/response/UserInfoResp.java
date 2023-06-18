package com.example.ssoauth.dto.response;

import com.example.ssoauth.domain.Permission;
import lombok.Data;

import java.util.List;

@Data
public class UserInfoResp {
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
}
