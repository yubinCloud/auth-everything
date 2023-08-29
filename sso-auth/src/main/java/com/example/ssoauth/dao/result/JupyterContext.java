package com.example.ssoauth.dao.result;

import lombok.Data;

/**
 * 登录 Jupyter 获得的上下文信息
 */
@Data
public class JupyterContext {

    private String token;
}
