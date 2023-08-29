package com.example.ssoauth.exchange.request;

import lombok.Data;

@Data
public class JupyterUsrCreateRequest {
    /**
     * 是否具有 jupyterhub 的管理权限
     */
    private Boolean admin;
}
