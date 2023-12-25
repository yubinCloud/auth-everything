package com.example.afleader.exchange;

import com.example.afleader.dto.response.R;
import com.example.afleader.exchange.request.dsworker.ExecuteSQLRequest;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.Map;

@HttpExchange(url = "/ds-worker")
public interface DsWorkerExchange {

    @PostExchange("/exec/select")
    R<List<Map<String, Object>>> executeSelectSQL(ExecuteSQLRequest requestBody);
}
