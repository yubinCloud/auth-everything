package com.example.ssoauth.domain;

import lombok.Data;

@Data
public class Permission {
    private String perm;
    private String remark;  // 权限描述
}
