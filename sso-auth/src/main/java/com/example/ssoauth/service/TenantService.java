package com.example.ssoauth.service;

import com.example.ssoauth.dao.result.TenantDao;
import com.example.ssoauth.dto.request.NewTenantDto;
import com.example.ssoauth.dto.request.UpdateTenantDto;
import com.example.ssoauth.dto.response.PageResp;
import com.example.ssoauth.entity.Tenant;
import com.example.ssoauth.mapper.TenantMapper;
import com.example.ssoauth.mapstructutil.TenantConverterUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TenantService {

    private final TenantMapper tenantMapper;
    private final TenantConverterUtil tenantConverterUtil;

    public PageResp<Tenant> selectByPage(int pageNum, int pageSize, String name) {
        PageInfo<Tenant> pageInfo = PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> tenantMapper.selectPageByPage(name).stream().map(tenantConverterUtil::toTenant).toList());

        PageResp<Tenant> pageResp = new PageResp<>();
        pageResp.setPageSize(pageInfo.getPageSize());
        pageResp.setPageNum(pageInfo.getPageNum());
        pageResp.setList(pageInfo.getList());
        pageResp.setTotal(pageInfo.getTotal());
        return pageResp;
    }

    @Transactional
    public void addTenant(NewTenantDto tenantDto) {
        TenantDao tenantDao = tenantConverterUtil.DtoToDao(tenantDto);
        tenantMapper.addTenant(tenantDao);
    }

    @Transactional
    public void deleteByName(String name) {
        tenantMapper.deleteByName(name);
    }

    @Transactional
    public void updateById(UpdateTenantDto req) {
        tenantMapper.updateById(req.getTenantId(),req.getName());
    }
}
