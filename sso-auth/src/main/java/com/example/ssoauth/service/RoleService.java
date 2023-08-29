package com.example.ssoauth.service;

import com.example.ssoauth.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;

    public String findOneRole(Integer roleId) {
        var role = roleMapper.selectByPrimaryKey(roleId);
        return role.getName();
    }
}
