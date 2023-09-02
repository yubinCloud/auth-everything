package com.example.ssoauth.dao.result;

import java.io.Serial;
import java.io.Serializable;

import cn.hutool.json.JSONArray;
import lombok.Data;

/**
 * 角色表
 * @TableName role
 */
@Data
public class RoleDao implements Serializable {
    /**
     * 角色 ID
     */
    private Integer roleId;

    /**
     * 角色名称
     */
    private String name;


    /**
     * 权限列表
     */
    private JSONArray permissionList;
    

    @Serial
    private static final long serialVersionUID = 1L;

}