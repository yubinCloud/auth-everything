package com.example.afleader.service;

import com.example.afleader.dto.request.CommonRouteCreateRequest;
import com.example.afleader.dto.request.SQLRouteCreateRequest;
import com.example.afleader.entity.HandlerCode;
import com.example.afleader.exception.DynamicRouteCreateException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RouteCodeService {

    private final AfWorkerService afWorkerService;

    public HandlerCode buildSQLRouteCode(String routeId, SQLRouteCreateRequest request) {
        var handlerCode = afWorkerService.getHandlerCodeForSQL(routeId, request.getSql(), request.getDsConf());
        return new HandlerCode(handlerCode.getHandlerName(), handlerCode.getHandlerCode());
    }

    public HandlerCode buildCommonRouteCode(String routeId, CommonRouteCreateRequest request) {
        HandlerCode result = new HandlerCode();
        String handlerName = "handler_" + routeId;
        String code = result.getCode();
        // 这里应当加入更多的对 `code` 的校验
        if (!code.startsWith("async def handler(request")) {
            throw new DynamicRouteCreateException("自定义代码格式不符合规范。开头必须以 `async def handler(request)` 开头");
        }
        code = code.replaceFirst("handler", handlerName);
        return new HandlerCode(handlerName, code);
    }

}
