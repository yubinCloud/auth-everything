package com.example.avuehelper.dao;

import java.io.Serializable;
import java.util.Date;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * 可视化表
 * @TableName blade_visual
 */
@Data
public class VisualDao implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 大屏标题
     */
    private String title;

    /**
     * 大屏背景
     */
    private String backgroundUrl;

    /**
     * 大屏类型
     */
    private Integer category;

    /**
     * 发布密码
     */
    private String password;

    /**
     * 创建人
     */
    private Long createUser;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private Long updateUser;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否已删除
     */
    private Integer isDeleted;

    /**
     * 对应用户的 username

     */
    private String loginid;

    /**
     * 存储大屏的变量值
     */
    private JSONObject variable;

}