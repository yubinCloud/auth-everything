package com.example.avuehelper.dao;

import cn.hutool.core.date.DateTime;
import lombok.Data;

@Data
public class VisualDBDao {

    private Integer id;
    private String name;
    private String driverClass;
    private String url;
    private String username;
    private String password;
    private String remark;
    private Integer createUser;
    private Integer createDept;
    private DateTime createTime;
    private Integer updateUser;
    private DateTime updateTime;
    private Integer status;
    private Integer isDeleted;
    private Integer tenantId;
    private String loginId;

}
