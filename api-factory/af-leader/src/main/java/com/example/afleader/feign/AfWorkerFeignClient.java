package com.example.afleader.feign;

import com.example.afleader.feign.request.afworker.HandlerCodeForSQLReq;
import com.example.afleader.feign.response.afworker.HandlerCodeForSQLResp;
import com.example.afleader.feign.response.afworker.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 与 af-worker 进行交互
 */
@FeignClient(name = "af-worker")
public interface AfWorkerFeignClient {

    @PostMapping("/meta/route/code/sql")
    R<HandlerCodeForSQLResp> getHandlerCodeForSQL(@RequestBody HandlerCodeForSQLReq body);
}
