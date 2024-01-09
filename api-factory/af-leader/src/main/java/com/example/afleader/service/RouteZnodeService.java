package com.example.afleader.service;

import com.example.afleader.dto.request.SQLRouteCreateRequest;
import com.example.afleader.entity.RouteZnodeData;
import com.example.afleader.entity.RouteZnodeMeta;
import com.example.afleader.enums.DynamicRouteTypeEnum;
import com.example.afleader.exception.DynamicRouteOpsException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class RouteZnodeService {

    /**
     * 用于在 code 中找到带有 rid 的 Endpoint name 的正则表达式
     */
    private static final Pattern UNQIUE_ENDPOINT_CLASS_NAME_PATTERN = Pattern.compile("(?<=class\\s)Endpoint_.*(?=\\(HTTPEndpoint)");

    /**
     * 用于在 code 中找到不带有 rid 的 Endpoint name 的正则表达式
     */
    private static final Pattern ORIGINAL_ENDPOINT_CLASS_NAME_PATTERN = Pattern.compile("(?<=class\\s)Endpoint(?=\\(HTTPEndpoint)");


    public RouteZnodeMeta createSQLRouteMeta(@NotNull SQLRouteCreateRequest createRequest) {
        RouteZnodeMeta meta = new RouteZnodeMeta();
        var dsConf = createRequest.getDsConf();
        Map<String, String> metaContent = new HashMap<>();
        metaContent.put("sql", createRequest.getSql());
        metaContent.put("ds.driver", dsConf.getDriverClass());
        metaContent.put("ds.url", dsConf.getUrl());
        metaContent.put("ds.un", dsConf.getUsername());
        metaContent.put("ds.pwd", dsConf.getPassword());
        meta.setRt(DynamicRouteTypeEnum.SQL_TYPE.getRtCode());
        meta.setContent(metaContent);
        return meta;
    }

    /**
     * 校验修改的 code 是否合法
     * @param routeZnodeData
     */
    public void checkRouteCode( @NotNull RouteZnodeData routeZnodeData) {
        Matcher m = ORIGINAL_ENDPOINT_CLASS_NAME_PATTERN.matcher(routeZnodeData.getCode());
        if (!m.find()) {
            throw new DynamicRouteOpsException("未找到 Endpoint class 定义");
        }
    }

    public RouteZnodeData originalHandlerName(@NotNull RouteZnodeData routeInfo) {
        String code = routeInfo.getCode();
        var matcher = UNQIUE_ENDPOINT_CLASS_NAME_PATTERN.matcher(code);
        code = matcher.replaceAll("Endpoint");
        routeInfo.setCode(code);
        return routeInfo;
    }

    public RouteZnodeData uniqueHandlerName(@NotNull RouteZnodeData routeInfo) {
        checkRouteCode(routeInfo);
        var matcher = ORIGINAL_ENDPOINT_CLASS_NAME_PATTERN.matcher(routeInfo.getCode());
        routeInfo.setCode(
                matcher.replaceAll(routeInfo.getHandler())
        );
        return routeInfo;
    }
}
