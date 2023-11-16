package com.example.eusersso.service;

import com.example.eusersso.converter.EuserConverter;
import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.PageResp;
import com.example.eusersso.util.SubsystemEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class EuserForPublicApiService {

    private final EuserService euserService;

    private final EuserConverter euserConverter;

    public int createEuser(NewUserDto newUserDto, String createdBy) {
        var euserDao = euserConverter.toEuserDao(newUserDto);
        euserDao.setCreatedBy(createdBy);
        euserDao.setLabels(new HashMap<>(){{
            put("access-pa", true);
        }});
        return euserService.insertOne(euserDao);
    }

    public PageResp<EuserListItem> selectPageByCond(String username, String screenName, Integer apiId,
                                                Integer pageNum, Integer pageSize) {
        return euserService.selectPageByCond(username, screenName, null, apiId, pageNum, pageSize, SubsystemEnum.PUBLIC_API);
    }

}
