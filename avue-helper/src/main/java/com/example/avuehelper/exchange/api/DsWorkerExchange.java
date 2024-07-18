package com.example.avuehelper.exchange.api;

import com.example.avuehelper.exchange.request.ExecSelectSQLRequest;
import com.example.avuehelper.exchange.response.DsWorkerRespJSON;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.Map;

@HttpExchange("/ds-worker")
public interface DsWorkerExchange {

    @PostExchange("/exec/select")
    DsWorkerRespJSON<List<Map<String, Object>>> execSelectSQL(@RequestBody ExecSelectSQLRequest body);
}
