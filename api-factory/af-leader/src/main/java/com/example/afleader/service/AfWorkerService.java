package com.example.afleader.service;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.example.afleader.entity.DatasourceConf;
import com.example.afleader.exception.DynamicRouteCreateException;
import com.example.afleader.feign.AfWorkerFeignClient;
import com.example.afleader.feign.request.afworker.HandlerCodeForSQLReq;
import com.example.afleader.feign.response.afworker.HandlerCodeForSQLResp;
import com.example.afleader.feign.response.afworker.R;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AfWorkerService {

    private final AfWorkerFeignClient afWorkerFeignClient;

    private final RestTemplate restTemplate;

    private final NacosService nacosService;

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
        var handlerCodeResp = afWorkerFeignClient.getHandlerCodeForSQL(req);
        if (handlerCodeResp.getCode() != 0) {
            throw new DynamicRouteCreateException("获取 handler code 失败");
        }
        return handlerCodeResp.getData();
    }

    void createRouteForAllWorkers(String znode) {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("znode", znode);
        List<Instance> workers;
        try {
            workers = nacosService.getWorkerInstances();
        } catch (Exception e) {
            log.error("NACOS 异常" + e.getMessage());
            throw new DynamicRouteCreateException("NACOS 发生异常，创建路由失败");
        }
        // 逐个 worker 调用 add route 接口
        workers.forEach(worker -> {
            String url = String.format("http://%s:%d/meta/route/add", worker.getIp(), worker.getPort());
            System.out.println("URL: " + url);
            var resp = restTemplate.postForObject(url, requestBody, R.class);
        });
    }

}
