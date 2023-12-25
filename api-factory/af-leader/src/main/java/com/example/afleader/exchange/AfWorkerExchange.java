package com.example.afleader.exchange;

import com.example.afleader.exchange.request.afworker.HandlerCodeForSQLReq;
import com.example.afleader.exchange.response.afworker.HandlerCodeForSQLResp;
import com.example.afleader.exchange.response.afworker.R;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

@HttpExchange
public interface AfWorkerExchange {

    @PostExchange("/meta/route/code/sql")
    R<HandlerCodeForSQLResp> getHandlerCodeForSQL(@RequestBody HandlerCodeForSQLReq body);

    @GetExchange("/meta/route/openapi")
    Map<String, Object> getOpenAPI(@RequestParam String path);
}
