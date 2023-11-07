package com.example.eusersso.dao;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 外部用户表
 * @TableName euser
 */
@Data
public class EuserDao implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int uid;

    private String username;

    private String password;

    private boolean checked;

    private String screenName;

    private String createdBy;

    private long createTime;

    private String note;

    private List<Integer> avueRoles;

    private List<Integer> publicApiRoles;
}