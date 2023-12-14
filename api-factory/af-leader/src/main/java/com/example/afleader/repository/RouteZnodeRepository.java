package com.example.afleader.repository;

import cn.hutool.json.JSONUtil;
import com.example.afleader.config.ConstantConfig;
import com.example.afleader.entity.RouteZnodeData;
import lombok.RequiredArgsConstructor;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.KeeperException;
import org.springframework.stereotype.Repository;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class RouteZnodeRepository {

    private final CuratorFramework curatorClient;

    public static final Charset ZNODE_DATA_CHARSET = StandardCharsets.UTF_8;

    public RouteZnodeData getRouteZnodeData(String znodePath) throws Exception {
        RouteZnodeData znodeData;
        try {
            znodeData = JSONUtil.toBean(new String(curatorClient.getData().forPath(znodePath), ZNODE_DATA_CHARSET), RouteZnodeData.class);
        } catch (KeeperException.NoNodeException exception) {
            return null;
        }
        return znodeData;
    }

    public byte[] convertRouteZnodeData(RouteZnodeData routeZnodeData) {
        return JSONUtil.parseObj(routeZnodeData, false).toString().getBytes(ZNODE_DATA_CHARSET);
    }

    public boolean isEnabled(String znodePath) throws Exception {
        return Objects.nonNull(curatorClient.checkExists().forPath(znodePath + ConstantConfig.ENABLED_FLAG_ZNODE));
    }

    public boolean isEncrypted(String znodePath) throws Exception {
        return Objects.nonNull(curatorClient.checkExists().forPath(znodePath + ConstantConfig.ENCRYPT_FLAG_ZNODE));
    }
}
