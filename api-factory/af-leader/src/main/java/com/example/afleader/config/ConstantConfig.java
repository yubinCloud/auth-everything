package com.example.afleader.config;


public class ConstantConfig {

    public static final String IUSER_TOKEN_HEADER = "Authz"; // 内部用户的请求中，存放 token 的 Header 字段

    public static final String ROUTE_ZK_NS = "/route";  // routes 在 Zookeeper 中的 namespace

    /**
     * 在一个 route 的 znode 下面标识是否需要加密访问的 znode 名称
     */
    public static final String ENCRYPT_FLAG_ZNODE = "/encrypt";

    /**
     * 在一个 route 的 znode 下面标识是否启用的 znode 名称
     */
    public static final String ENABLED_FLAG_ZNODE = "/enabled";
}
