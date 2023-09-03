package com.example.ssoauth.entity;


import java.io.Serializable;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
* 角色表
* @TableName role
*/
@Data
public class Role implements Serializable {

    /**
    * 角色 ID
    */
    @NotNull(message="[角色 ID]不能为空")
    private Integer id;
    /**
    * 角色名称
    */
    @NotBlank(message="[角色名称]不能为空")
    @Size(max= 255,message="编码长度不能超过255")
    @Length(max= 255,message="编码长度不能超过255")
    private String name;
    /**
    * 该角色所拥有的的权限
    */
    @NotNull(message="[该角色所拥有的的权限]不能为空")
    private List<String> permissionList;

}
