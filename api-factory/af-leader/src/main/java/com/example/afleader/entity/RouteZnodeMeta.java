package com.example.afleader.entity;

import lombok.Data;

import java.util.Map;

@Data
public class RouteZnodeMeta {

    /**
     * route type
     */
    private int rt;

    /**
     * 元信息的内容
     */
    private Map<String, String> content;

}
