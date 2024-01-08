package com.example.afleader.service;

import com.example.afleader.dto.request.SQLRouteCreateRequest;
import com.example.afleader.entity.RouteZnodeMeta;
import com.example.afleader.enums.DynamicRouteTypeEnum;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RouteZnodeService {

    public RouteZnodeMeta createSQLRouteMeta(SQLRouteCreateRequest createRequest) {
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
}
