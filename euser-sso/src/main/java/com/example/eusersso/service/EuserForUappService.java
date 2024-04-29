package com.example.eusersso.service;

import com.example.eusersso.converter.EuserConverter;
import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.PageResp;
import com.example.eusersso.util.ConstantUtil;
import com.example.eusersso.util.PermissionCheckUtil;
import com.example.eusersso.util.SubsystemEnum;
import com.example.eusersso.util.TimestampUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class EuserForUappService {

    private final EuserService euserService;

    private final EuserConverter euserConverter;
    @Resource
    private PermissionCheckUtil permissionCheckUtil;



    public int createEuser(NewUserDto newUser, String createdBy, Integer myTenantId) {

        //校验管理员权限等级
        boolean permission = permissionCheckUtil.superAdminCheck(createdBy, myTenantId);

        if (newUser.getTenantId() == null){
            newUser.setTenantId(ConstantUtil.DEFAULT_TENANT_ID);
        }
        //如果没有super-admin权限,则需要将新用户的tenantId与当前管理员同步
        if ( !permission && myTenantId != newUser.getTenantId()){
            newUser.setTenantId(myTenantId);
        }

        var euserDao = euserConverter.toEuserDao(newUser);
        euserDao.setCreatedBy(createdBy);
        euserDao.setLabels(new HashMap<>() {{
            put(SubsystemEnum.UAPP.getDbAccessLabel(), true);
        }});
        return euserService.insertOne(euserDao);
    }

    public PageResp<EuserListItem> selectPageByCond(String username, String screenName, Integer uappRole, Integer tenantId,
                                                    Integer pageNum, Integer pageSize) {
        return euserService.selectPageByCond(username, screenName, null, tenantId,null,
                uappRole, pageNum, pageSize, SubsystemEnum.UAPP);
    }
}
