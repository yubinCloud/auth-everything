package com.example.eusersso.service;

import com.example.eusersso.converter.AvueRoleConverter;
import com.example.eusersso.converter.EuserConverter;
import com.example.eusersso.dao.param.AvueRoleSelectCond;
import com.example.eusersso.dto.request.NewAvueRoleDto;
import com.example.eusersso.dto.request.NewUserDto;
import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.dto.response.PageResp;
import com.example.eusersso.entity.AvueRole;
import com.example.eusersso.mapper.AvueRoleMapper;
import com.example.eusersso.repository.AvueRoleRepository;
import com.example.eusersso.util.SubsystemEnum;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class EuserForAvueService {

    private final EuserService euserService;

    private final EuserConverter euserConverter;

    private final AvueRoleConverter avueRoleConverter;

    private final AvueRoleMapper avueRoleMapper;

    private final AvueRoleRepository avueRoleRepository;

    public int createEuser(NewUserDto newUser, String createdBy) {
        var euserDao = euserConverter.toEuserDao(newUser);
        euserDao.setCreatedBy(createdBy);
        euserDao.setLabels(new HashMap<>() {{
            put(SubsystemEnum.AVUE.getDbAccessLabel(), true);
        }});
        return euserService.insertOne(euserDao);
    }

    public PageResp<EuserListItem> selectPageByCond(String username, String screenName, Integer roleId,
                                                    Integer pageNum, Integer pageSize) {
        return euserService.selectPageByCond(username, screenName, roleId, null, pageNum, pageSize, SubsystemEnum.AVUE);
    }

    @Transactional
    public AvueRole createRole(NewAvueRoleDto newRoleDto) {
        AvueRole role = avueRoleConverter.toAvueRole(newRoleDto);
        avueRoleMapper.insertOne(role);
        return role;
    }

    @Transactional
    public int updateRole(AvueRole avueRole) {
        int result = avueRoleMapper.updateOne(avueRole);
        if (avueRole.getName() != null) {
            avueRoleRepository.updateRole(avueRole.getRoleId(), avueRole.getName());
        }
        return result;
    }

    public PageInfo<AvueRole> selectAvueRolePageByCond(AvueRoleSelectCond cond, int pageNum, int pageSize) {
        return PageHelper.startPage(pageNum, pageSize).doSelectPageInfo(
                () -> avueRoleMapper.selectByCond(cond)
        );
    }

    public int deleteRole(int roleId) {
        return avueRoleMapper.deleteById(roleId);
    }
}