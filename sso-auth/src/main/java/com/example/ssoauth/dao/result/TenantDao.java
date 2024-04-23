package com.example.ssoauth.dao.result;

import lombok.Data;
/**
 * 租户表
 * @TableName tenant
 */
@Data
public class TenantDao {

    private Integer tenantId;

    private String name;

}
