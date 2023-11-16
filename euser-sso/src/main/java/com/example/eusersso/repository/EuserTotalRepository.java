package com.example.eusersso.repository;

import com.example.eusersso.dao.param.EuserSelectCond;
import com.example.eusersso.mapper.EuserMapper;
import com.example.eusersso.util.SubsystemEnum;
import lombok.RequiredArgsConstructor;
import net.spy.memcached.MemcachedClient;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class EuserTotalRepository {

    private final MemcachedClient memcachedClient;

    private final EuserMapper euserMapper;

    // 在 memcached 中缓存用户总量的 key
    public static final Map<SubsystemEnum, String> EUSER_TOTAL_KEY_IN_MEMCACHED = new HashMap<>() {{
        put(SubsystemEnum.AVUE, "ut:avue");
        put(SubsystemEnum.PUBLIC_API, "ut:pa");
    }};

    public int getEuserTotal(EuserSelectCond cond, SubsystemEnum subsystem) {
        int total;
        if (Objects.isNull(cond.getUsername())
                && Objects.isNull(cond.getScreenName())
                && Objects.isNull(cond.getAvueRoleId())
                && Objects.isNull(cond.getApiId())
        ) {
            Object cached = memcachedClient.get(EUSER_TOTAL_KEY_IN_MEMCACHED.get(subsystem));
            if (Objects.isNull(cached)) {
                total = updateEuserTotal(subsystem);
            } else {
                total = Integer.parseInt((String) cached);
            }
        } else {
            total = euserMapper.countByCond(cond);
        }
        return total;
    }

    public int updateEuserTotal(SubsystemEnum subsystem) {
        int total = switch (subsystem) {
            case AVUE -> euserMapper.userTotalOfAvue();
            case PUBLIC_API -> euserMapper.userTotalOfPublicAPI();
        };
        String key = EUSER_TOTAL_KEY_IN_MEMCACHED.get(subsystem);
        memcachedClient.set(key, 10, Integer.toString(total));
        return total;
    }
}
