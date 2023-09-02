package com.example.ssoauth.repository;

import com.example.ssoauth.dao.result.RoleDao;
import com.example.ssoauth.mapper.RoleMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class RoleLookupTable {

    private RoleMapper roleMapper;

    @Autowired
    public void setRoleMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    private Map<Integer, RoleDao> roleTable;

    @PostConstruct
    public void init() {
        List<RoleDao> roles = roleMapper.selectAll();
        roleTable = roles.stream().collect(Collectors.toMap(RoleDao::getRoleId, Function.identity()));
    }

    public RoleDao selectOne(Integer roleId) {
        return roleTable.get(roleId);
    }

    public List<RoleDao> selectAll() {
        return roleTable.values().stream().toList();
    }

    public void addOne(RoleDao roleDao) {
        roleTable.put(roleDao.getRoleId(), roleDao);
    }

    public List<RoleDao> translateRoleIds(List<Integer> roleIds) {
        return roleIds.stream()
                .map(roleTable::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
