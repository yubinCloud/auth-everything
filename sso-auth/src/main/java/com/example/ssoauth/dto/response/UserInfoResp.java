package com.example.ssoauth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "查看用户列表")
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
     * 角色列表
     */
    private List<String> roleList;

    /**
     * 权限列表
     */
    private List<String> permissionList;

    /**
     * 用户描述
     */
    private String note;

    @Schema(description = "用户创建时间的 Unix 时间戳，是从1970年1月1日开始的秒数，JS 转换代码：`new Date(parseInt(d) * 1000)`")
    private Long createTime;
}
