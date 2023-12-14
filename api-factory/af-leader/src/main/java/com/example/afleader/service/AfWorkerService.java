package com.example.afleader.service;

import com.example.afleader.entity.DatasourceConf;
import com.example.afleader.exception.DynamicRouteOpsException;
import com.example.afleader.exchange.AfWorkerExchange;
import com.example.afleader.exchange.request.afworker.HandlerCodeForSQLReq;
import com.example.afleader.exchange.response.afworker.HandlerCodeForSQLResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AfWorkerService {

    private final AfWorkerExchange afWorkerExchange;

    /**
     * 调用 af-worker 来获取 SQL Route 的 handler code
     * @param routeId
     * @param sql
     * @param datasourceConf
     * @return
     */
    HandlerCodeForSQLResp getHandlerCodeForSQL(String routeId, String sql, DatasourceConf datasourceConf) {
        Map<String, String> dsConf = new HashMap<>();
        dsConf.put("driver_class", datasourceConf.getDriverClass());
        dsConf.put("url", datasourceConf.getUrl());
        dsConf.put("username", datasourceConf.getUsername());
        dsConf.put("password", datasourceConf.getPassword());
        var req = new HandlerCodeForSQLReq();
        req.setRouteId(routeId);
        req.setSql(sql);
        req.setDsConf(dsConf);
        log.info("prepare to request af-worker");
        var handlerCodeResp = afWorkerExchange.getHandlerCodeForSQL(req);
        log.info("recv af-worker response");
        if (handlerCodeResp.getCode() != 0) {
            throw new DynamicRouteOpsException("获取 handler code 失败");
        }
        return handlerCodeResp.getData();
    }

    public Map<String, Object> getOpenAPI(String routePath) {
        return afWorkerExchange.getOpenAPI(routePath);
    }

}
