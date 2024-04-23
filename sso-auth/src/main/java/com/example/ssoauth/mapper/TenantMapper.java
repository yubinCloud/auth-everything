package com.example.ssoauth.mapper;

import com.example.ssoauth.dao.result.TenantDao;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;


import java.util.Collection;
import java.util.List;

/**
 * @author zyx
 * @description 针对表【tenant(租户表)】的数据库操作Mapper
 * @createDate 2024-04-15 10:24:56
 * @Entity com.example.ssoauth.dao.Tenant
 */
@Mapper
public interface TenantMapper {
    void addTenant(TenantDao tenantDao);

    void deleteByName(@Param("name") String name);

    List<TenantDao> selectPageByPage(@Param("name")String name);

    void updateById(@Param("tenantId")Integer tenantId, @Param("name")String name);
}
