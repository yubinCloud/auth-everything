package com.example.afleader.service;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NacosService {

    private final NamingService namingService;

    public static final String AF_WORKER_SERVICE_NAME = "af-worker";

    public List<Instance> getWorkerInstances() throws NacosException {
        return namingService.getAllInstances(AF_WORKER_SERVICE_NAME);
    }
}
