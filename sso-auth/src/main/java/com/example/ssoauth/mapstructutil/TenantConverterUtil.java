package com.example.ssoauth.mapstructutil;

import com.example.ssoauth.dao.result.TenantDao;
import com.example.ssoauth.dto.request.NewTenantDto;
import com.example.ssoauth.entity.Tenant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantConverterUtil {
    public TenantDao DtoToDao(NewTenantDto tenantDto){
        if (tenantDto == null){
            return null;
        }
        TenantDao tenantDao = new TenantDao();
        tenantDao.setName(tenantDto.getName());

        return tenantDao;
    }

    public Tenant toTenant(TenantDao tenantDao) {
        if (tenantDao == null){
            return null;
        }
        Tenant tenant = new Tenant();
        tenant.setTenantId(tenantDao.getTenantId());
        tenant.setName(tenantDao.getName());

        return tenant;
    }
}
