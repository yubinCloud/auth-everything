package com.example.ssoauth.repository;

import com.example.ssoauth.dao.result.RoleDao;
import com.example.ssoauth.mapper.RoleMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class RoleLookupTable {

    private RoleMapper roleMapper;

    @Autowired
    public void setRoleMapper(RoleMapper roleMapper) {
        this.roleMapper = roleMapper;
    }

    private final Map<Integer, RoleDao> roleTable = new HashMap<>();  // WARNING: 共享资源，需要进行并发控制

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    private final Lock rLock = rwLock.readLock();

    private final Lock wLock = rwLock.writeLock();

    @PostConstruct
    public void init() {
        loadData();
    }

    public RoleDao selectOne(Integer roleId) {
        RoleDao result;
        rLock.lock();
        try {
            result = roleTable.get(roleId);
        } finally {
            rLock.unlock();
        }
        return result;
    }

    public List<RoleDao> selectAll() {
        List<RoleDao> result;
        rLock.lock();
        try {
            result = roleTable.values().stream().toList();
        } finally {
            rLock.unlock();
        }
        return result;
    }

    public void addOne(RoleDao roleDao) {
        wLock.lock();
        try {
            roleTable.put(roleDao.getRoleId(), roleDao);
        } finally {
            wLock.unlock();
        }
    }

    public void deleteOne(int roleId) {
        wLock.lock();
        try {
            roleTable.remove(roleId);
        } finally {
            wLock.unlock();
        }
    }

    public List<RoleDao> translateRoleIds(List<Integer> roleIds) {
        List<RoleDao> result;
        rLock.lock();
        try {
            result = roleIds.stream()
                    .map(roleTable::get)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        } finally {
            rLock.unlock();
        }
        return result;
    }

    /**
     * 将数据库中的角色信息全部加载到 table 中
     */
    private void loadData() {
        List<RoleDao> roles = roleMapper.selectAll();
        final var newRoleTable = roles.stream().collect(Collectors.toMap(RoleDao::getRoleId, Function.identity()));
        wLock.lock();
        try {
            roleTable.clear();
            roleTable.putAll(newRoleTable);
        } finally {
            wLock.unlock();
        }
    }
}
