package com.example.afleader.entity;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * Zookeeper 中 Route 节点的数据
 */
@Data
@Builder
public class RouteZnodeData {

    /**
     * route path
     */
    private String path;

    /**
     * route id
     */
    private String rid;

    /**
     * route name
     */
    private String name;

    private String desc;

    /**
     * 接口的访问方式
     */
    private List<String> methods;

    /**
     * handler name
     */
    private String handler;

    /**
     * handler code
     */
    private String code;
}
