package com.example.ssoauth.entity;

import lombok.Data;
/**
 * 租户表
 * @TableName tenant
 */
@Data
public class Tenant {
    private Integer tenantId;
    private String name;
}
