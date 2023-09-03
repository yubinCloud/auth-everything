package com.example.ssoauth.service;

import com.example.ssoauth.dao.result.RoleDao;
import com.example.ssoauth.entity.Role;
import com.example.ssoauth.mapper.RoleMapper;
import com.example.ssoauth.mapstruct.RoleConverter;
import com.example.ssoauth.repository.RoleLookupTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;

    private final RoleLookupTable roleLookupTable;

    private RoleConverter roleConverter;

    public String findById(Integer roleId) {
        var role = roleMapper.selectByPrimaryKey(roleId);
        return role.getName();
    }

    public List<Role> findAll() {
        return roleLookupTable.selectAll()
                .stream()
                .map(roleDao -> roleConverter.toRole(roleDao))
                .toList();
    }

    @Transactional
    public int deleteRole(int roleId) {
        var result = roleMapper.deleteByPrimaryKey(roleId);
        if (result == 1) {
            roleLookupTable.deleteOne(roleId);
        }
        return result;
    }
}
