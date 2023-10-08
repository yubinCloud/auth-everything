package com.example.ssoauth.service;

import com.example.ssoauth.dao.result.RoleDao;
import com.example.ssoauth.dao.result.UserDao;
import com.example.ssoauth.dto.request.UpdateRoleDto;
import com.example.ssoauth.entity.Role;
import com.example.ssoauth.mapper.RoleMapper;
import com.example.ssoauth.mapstruct.RoleConverter;
import com.example.ssoauth.mapstructutil.CommonConverterUtil;
import com.example.ssoauth.repository.RoleLookupTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;

    private final RoleLookupTable roleLookupTable;

    private final RoleConverter roleConverter;

    public Role findById(Integer roleId) {
        var role = roleMapper.selectByPrimaryKey(roleId);
        if (role == null) {
            return null;
        }
        return roleConverter.toRole(role);
    }

    public List<Role> findAll() {
        return roleLookupTable.selectAll()
                .stream()
                .map(roleConverter::toRole)
                .filter(Objects::nonNull)
                .toList();
    }

    public int addRole(RoleDao newRoleDao) {
        int cnt = roleMapper.insertOne(newRoleDao);
        if (cnt >= 1) {
            roleLookupTable.reloadData();
        }
        return cnt;
    }

    @Transactional
    public int deleteRole(int roleId) {
        var result = roleMapper.deleteByPrimaryKey(roleId);
        if (result == 1) {
            roleLookupTable.reloadData();
        }
        return result;
    }

    @Transactional
    public void updateRole(UpdateRoleDto role) {
        if (role.getName() != null) {
            roleMapper.updateName(role.getRoleId(), role.getName());
        }
        if (role.getAdd().size() != 0) {
            roleMapper.addPermissions(role.getRoleId(), CommonConverterUtil.strListToJsonArray(role.getAdd()));
        }
        role.getDel().stream().filter(Objects::nonNull).forEach(perm -> roleMapper.deletePermission(role.getRoleId(), perm));
        roleLookupTable.reloadData();
    }
}
