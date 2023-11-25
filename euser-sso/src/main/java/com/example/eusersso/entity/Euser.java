package com.example.eusersso.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Euser {

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

    /** avue 的权限信息 */
    private List<AvuePermission> avueAccessList;

    /** 用户可访问的 API 的列表 */
    private List<PublicApiPermission> apiAccessList;

}
