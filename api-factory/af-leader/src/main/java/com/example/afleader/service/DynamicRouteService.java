package com.example.afleader.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.example.afleader.config.ConstantConfig;
import com.example.afleader.dto.request.CommonRouteCreateRequest;
import com.example.afleader.dto.request.SQLRouteCreateRequest;
import com.example.afleader.dto.response.RouteCreateResponse;
import com.example.afleader.dto.response.RouteInfoResponse;
import com.example.afleader.entity.HandlerCode;
import com.example.afleader.entity.RouteZnodeData;
import com.example.afleader.exception.DynamicRouteOpsException;
import com.example.afleader.repository.RouteZnodeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class DynamicRouteService {

    private final RouteCodeService routeCodeService;

    private final CuratorFramework curatorClient;

    private final RoutePathService routePathService;

    private final RouteZnodeRepository routeZnodeRepository;

    @PostConstruct
    public void postConstructInit() throws Exception {
        // 检查 ROUTE_NS 是否存在，不存在的话则创建该 znode
        var isExists = curatorClient.checkExists().forPath(ConstantConfig.ROUTE_ZK_NS);
        if (Objects.isNull(isExists)) {
            curatorClient.create().forPath(ConstantConfig.ROUTE_ZK_NS);
        }
    }

    /**
     * 创建 SQL 类型的 Route
     * @param request
     * @return
     * @throws Exception
     */
    public RouteCreateResponse createSQLRoute(SQLRouteCreateRequest request) throws Exception {
        String routeId = IdUtil.fastSimpleUUID();
        log.info("routeId: " + routeId);
        HandlerCode handlerCode = routeCodeService.buildSQLRouteCode(routeId, request);
        return createRoute(request.getPath(), request.getName(), request.getDescription(), routeId, request.getEncrypt(), handlerCode);
    }

    /**
     * 创建 Common 类型的 Route
     * @param request
     * @return
     * @throws Exception
     */
    public RouteCreateResponse createCommonRoute(CommonRouteCreateRequest request) throws Exception {
        String routeId = IdUtil.fastSimpleUUID();
        HandlerCode handlerCode = routeCodeService.buildCommonRouteCode(routeId, request);
        return createRoute(request.getPath(), request.getName(), request.getDescription(), routeId, request.getEncrypt(), handlerCode);
    }

    /**
     * 设置是否启用这个 route
     * @param routePath
     * @param enableFlag
     * @return
     * @throws Exception
     */
    public boolean enableRoute(String routePath, boolean enableFlag) throws Exception {
        return setRouteFlag(routePath, enableFlag, ConstantConfig.ENABLED_FLAG_ZNODE);
    }

    /**
     * 设置是否加密这个 route
     * @param routePath
     * @param encryptFlag
     * @return
     * @throws Exception
     */
    public boolean encryptRoute(String routePath, boolean encryptFlag) throws Exception {
        return setRouteFlag(routePath, encryptFlag, ConstantConfig.ENCRYPT_FLAG_ZNODE);
    }

    /**
     * 获取路由的信息
     * @param routePath
     * @return
     */
    public RouteInfoResponse getRouteInfo(String routePath) {
        String znodePath = routePathService.toZnodePath(routePath);
        RouteInfoResponse result = new RouteInfoResponse();
        try {
            result.setBasic(routeZnodeRepository.getRouteZnodeData(znodePath));
            result.setEnabled(routeZnodeRepository.isEnabled(znodePath));
            result.setEncrypted(routeZnodeRepository.isEncrypted(znodePath));
        } catch (Exception e) {
            return null;
        }
        return result;
    }

    public boolean updateRoute(String routePath, String routeName, List<String> methods, String code) throws Exception {
        String znodePath = routePathService.toZnodePath(routePath);
        RouteZnodeData znodeData = routeZnodeRepository.getRouteZnodeData(znodePath);
        if (Objects.isNull(znodeData)) {
            return false;
        }
        if (!StrUtil.isBlank(routeName)) {
            znodeData.setName(routeName);
        }
        if (Objects.nonNull(methods) && methods.size() > 0) {
            znodeData.setMethods(methods);
        }
        if (!StrUtil.isBlank(code)) {
            znodeData.setCode(code);
        }
        byte[] znodeBytes = routeZnodeRepository.convertRouteZnodeData(znodeData);
        curatorClient.setData().forPath(znodePath, znodeBytes);
        return true;
    }

    public boolean deleteRoute(String routePath) throws Exception {
        String znodePath = routePathService.toZnodePath(routePath);
        curatorClient.delete().deletingChildrenIfNeeded().inBackground().forPath(znodePath);
        return true;
    }

    private RouteCreateResponse createRoute(String routePath, String routeName, String desc, String rid, Boolean encrypt, HandlerCode handlerCode) throws Exception {
        final String znodeName = routePathService.toInternal(routePath);
        final String znodePath = ConstantConfig.ROUTE_ZK_NS + "/" + znodeName;
        var isExists = curatorClient.checkExists().forPath(znodePath);
        if (Objects.nonNull(isExists)) {
            throw new DynamicRouteOpsException("API 路径已存在，创建路由失败");
        }
        byte[] znodeData = routeZnodeRepository.convertRouteZnodeData(
                RouteZnodeData.builder().path(routePath).rid(rid).name(routeName).desc(desc).methods(List.of("POST")).handler(handlerCode.getName()).code(handlerCode.getCode()).build()
        );
        curatorClient.create().forPath(znodePath, znodeData);
        if (Objects.nonNull(encrypt) && encrypt.equals(true)) {
            curatorClient.create().forPath(znodePath + ConstantConfig.ENCRYPT_FLAG_ZNODE);
        }
//        afWorkerService.createRouteForAllWorkers(znodeName);  // 这里需要引入分布式事务
        return new RouteCreateResponse(handlerCode.getCode(), routePath);
    }

    /**
     * 为一个 route 的 znode 设置一个 flag，即该 znode 下的一个 children znode
     * @param routePath route path
     * @param flag 是否存在这个 flag，即是否存在这个 children znode
     * @param flagZnode znode 的名称，比如 `/enable`
     * @return
     * @throws Exception
     */
    private boolean setRouteFlag(String routePath, boolean flag, String flagZnode) throws Exception {
        String znodePath = routePathService.toZnodePath(routePath);
        // 检查路由是否存在
        if (Objects.isNull(curatorClient.checkExists().forPath(znodePath))) {
            return false;
        }
        String encryptPth = znodePath + flagZnode;
        // 开启 or 关闭
        if (flag) {
            try {
                curatorClient.create().forPath(encryptPth);
            } catch (Exception e) {
                return true;
            }
        } else {
            curatorClient.delete().forPath(encryptPth);
        }
        return true;
    }
}
