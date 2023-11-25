package com.example.eusersso.dao;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

    private String mobile;

    private boolean checked;

    private String screenName;

    private String createdBy;

    private long createTime;

    private String note;

    private Map<String, Object> labels;

    private String lastUpdatedIuser;

    private Long lastUpdatedTime;

    private List<Integer> avueRoles;

    private List<Integer> publicApiIds;
}