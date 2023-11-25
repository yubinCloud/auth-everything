package com.example.eusersso.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 对接山东通单点登录的相关参数
 */
@Configuration
@ConfigurationProperties(prefix = "sdt")
@Data
public class ShanDongTongProperties {

    /**
     * 是否开启对接山东通
     */
    private Boolean enabled;

    /**
     * 山东通平台接口地址
     */
    private String endpointUrl;

    /**
     * 对接参数：组织 ID
     */
    private String corpId;

    /**
     * 对接参数：应用 ID
     */
    private String agentId;

    /**
     * 对接参数：应用的凭证秘钥
     */
    private String secret;
}
