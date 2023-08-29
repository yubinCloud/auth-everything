package com.example.ssoauth.exchange.request;

import lombok.Data;

@Data
public class JupyterUserDelRequest {
    /**
     * 是否删除用户主目录文件
     * 枚举值 0 1
     */
    private String delete_all;
}
