package com.example.jupytergateway.exchange;

import com.example.jupytergateway.exchange.response.JupyterContext;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface AuthExchange {

    /**
     * 获取 Jupyter 的上下文信息
     * @param username
     * @return
     */
    @GetExchange("/internal/user/jupyter/ctx")
    String getJupyterContext(@RequestParam String username);

    @GetExchange("/internal/user/jupyter/ctxByToken")
    String getJupyterContextByToken(@RequestParam String token);
}
