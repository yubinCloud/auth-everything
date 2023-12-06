package com.example.afleader.service;

import com.example.afleader.config.ConstantConfig;
import org.springframework.stereotype.Service;

@Service
public class RoutePathService {

    /**
     * 将用户输入的 router path 转为内部的表示形式
     * @param rawPath
     * @return
     */
    public String toInternal(String rawPath) {
        return rawPath.replaceAll("/", ".");
    }

    public String toZnodePath(String routePath) {
        return ConstantConfig.ROUTE_ZK_NS + "/" + toInternal(routePath);
    }
}
