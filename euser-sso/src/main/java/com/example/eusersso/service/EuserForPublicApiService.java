package com.example.eusersso.service;

import com.example.eusersso.converter.EuserConverter;
import com.example.eusersso.dao.EuserDao;
import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.PageResp;
import com.example.eusersso.mapper.EuserMapper;
import com.example.eusersso.repository.AfRoutePermRepository;
import com.example.eusersso.util.SubsystemEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EuserForPublicApiService {

    private final EuserService euserService;

    private final EuserConverter euserConverter;

    private final EuserMapper euserMapper;

    private final AfRoutePermRepository afRoutePermRepository;

    public int createEuser(NewUserDto newUserDto, String createdBy) {
        var euserDao = euserConverter.toEuserDao(newUserDto);
        euserDao.setCreatedBy(createdBy);
        euserDao.setLabels(new HashMap<>(){{
            put(SubsystemEnum.PUBLIC_API.getDbAccessLabel(), true);
        }});
        return euserService.insertOne(euserDao);
    }

    public PageResp<EuserListItem> selectPageByCond(String username, String screenName, String routePath,
                                                Integer pageNum, Integer pageSize) {
        return euserService.selectPageByCond(username, screenName, null, routePath, pageNum, pageSize, SubsystemEnum.PUBLIC_API);
    }

    public List<String> queryPermissionList(String username) {
        return afRoutePermRepository.getPermList(username);
    }

    public void addPublicAPIPermission(String username, List<String> routes) {
        afRoutePermRepository.addPermission(username, routes);
    }

    public void deletePublicAPIPermission(String username, String apiId) {
        afRoutePermRepository.deletePermission(username, apiId);
    }

    public EuserDao queryCheckedByUsername(String username) {
        return euserMapper.queryCheckedByUsernameInPublicAPI(username);
    }

}
