package com.example.eusersso.service;

import com.example.eusersso.converter.EuserConverter;
import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.PageResp;

import com.example.eusersso.mapper.EuserMapper;
import com.example.eusersso.repository.AfRoutePermRepository;
import com.example.eusersso.util.ConstantUtil;
import com.example.eusersso.util.PermissionCheckUtil;
import com.example.eusersso.util.SubsystemEnum;
import com.example.eusersso.util.TimestampUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EuserForPublicApiService {

    private final EuserService euserService;

    private final EuserConverter euserConverter;

    private final EuserMapper euserMapper;

    private final AfRoutePermRepository afRoutePermRepository;

    @Resource
    private PermissionCheckUtil permissionCheckUtil;


    @Transactional
    public int createEuser(NewUserDto newUserDto, String createdBy, Integer tenantId) {
        //校验管理员权限等级
        boolean permission = permissionCheckUtil.superAdminCheck(createdBy, tenantId);

        if (newUserDto.getTenantId() == null) {
            newUserDto.setTenantId(ConstantUtil.DEFAULT_TENANT_ID);
        }
        //如果没有super-admin权限,则需要将新用户的tenantId与当前管理员同步
        if (!permission && tenantId != newUserDto.getTenantId()) {
            newUserDto.setTenantId(tenantId);
        }

        var euserDao = euserConverter.toEuserDao(newUserDto);
        euserDao.setCreatedBy(createdBy);
        euserDao.setLabels(new HashMap<>() {{
            put(SubsystemEnum.PUBLIC_API.getDbAccessLabel(), true);
        }});
        return euserService.insertOne(euserDao);
    }

    public PageResp<EuserListItem> selectPageByCond(String username, String screenName, String routePath, Integer tenantId,
                                                    Integer pageNum, Integer pageSize) {
        return euserService.selectPageByCond(username, screenName, null, tenantId, routePath,
                null, pageNum, pageSize, SubsystemEnum.PUBLIC_API);
    }

    public List<String> queryPermissionList(String username, Integer tenantId) {
        return afRoutePermRepository.queryPermListInDB(username, tenantId);
    }

    @Transactional
    public void addPublicAPIPermission(String username, Integer tenantId, List<String> routes) {
        afRoutePermRepository.addPermission(username, tenantId, routes);
    }

    public void deletePublicAPIPermission(String username, Integer tenantId, String apiId) {
        afRoutePermRepository.deletePermission(username, tenantId, apiId);
    }

    public List<String> queryCheckedByUsername(String username) {
        return afRoutePermRepository.queryCheckedPermList(username);
    }

}
