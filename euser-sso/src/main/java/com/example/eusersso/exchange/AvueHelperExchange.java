package com.example.eusersso.exchange;

import com.example.eusersso.dto.response.R;
import com.example.eusersso.exchange.request.avuehelper.BatchQueryVisualNameRequest;
import com.example.eusersso.exchange.response.avuehelper.VisualName;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.Map;

@HttpExchange
public interface AvueHelperExchange {

    @PostExchange("/internal/visual-name/batch")
    R<Map<String, VisualName>> batchQueryVisualName(@RequestBody BatchQueryVisualNameRequest requestBody);
}
