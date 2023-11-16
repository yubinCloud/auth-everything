package com.example.eusersso.repository;

import com.example.eusersso.dto.response.EuserListItem;
import com.example.eusersso.mapper.AvueRoleMapper;
import com.example.eusersso.util.ConstantUtil;
import lombok.RequiredArgsConstructor;
import net.spy.memcached.MemcachedClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class AvueRoleRepository {

    private final AvueRoleMapper avueRoleMapper;

    private final MemcachedClient memcachedClient;

    /**
     * 根据角色 ID 列表获取角色的名字列表
     * @param roleIds
     * @return
     */
    public List<EuserListItem.SimpleRole> getRoleNames(List<Integer> roleIds) {
        List<EuserListItem.SimpleRole> result = new ArrayList<>();
        Set<Integer> missed = new HashSet<>(roleIds);
        Map<String, Object> cacheResult = memcachedClient.getBulk(roleIds.stream().map(id -> ConstantUtil.MEMCACHED_KEY_PREFIX_OF_ROLE_NAME + id).toList());
        cacheResult.forEach((String k, Object v) -> {
            if (!Objects.isNull(v)) {
                int roleId = Integer.parseInt(k.substring(3));
                result.add(EuserListItem.SimpleRole.of(roleId, (String) v));
                missed.remove(roleId);
            }
        });
        if (missed.isEmpty()) {
            return result;
        }
        Map<Integer, String> missedNames = new HashMap<>();
        System.out.println(avueRoleMapper.selectBatchById(missed.stream().toList()));
        avueRoleMapper.selectBatchById(missed.stream().toList()).forEach(role -> missedNames.put(role.getRoleId(), role.getName()));
        missedNames.forEach((Integer roleId, String roleName) -> {
            memcachedClient.set(ConstantUtil.MEMCACHED_KEY_PREFIX_OF_ROLE_NAME + roleId, 604800, roleName);
            result.add(EuserListItem.SimpleRole.of(roleId, roleName));
        });
        return result;
    }

    public void updateRole(int roleId, String roleName) {
        memcachedClient.delete(ConstantUtil.MEMCACHED_KEY_PREFIX_OF_ROLE_NAME + roleId);
        return;
    }
}
