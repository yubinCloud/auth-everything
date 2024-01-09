package com.example.afleader.service;

import com.example.afleader.entity.RouteZnodeData;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;


@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RouteZnodeServiceTest {

    @Autowired
    private RouteZnodeService routeZnodeService;

    /**
     * 测试 `originalHandlerName()` 和 `uniqueHandlerName()`
     */
    @Test
    public void testConvertHandlerName() {
        final String RID = "ff22931c23704dd8a767d397f1e54f7e";
        final String handler = "Endpoint_" + RID;
        final String uniqueCode = String.format("class Endpoint_%s(HTTPEndpoint):\\n    async def post(self, request):\\n    pass", RID);
        final String originalCode = "class Endpoint(HTTPEndpoint):\\n    async def post(self, request):\\n    pass";
        var routeInfo = RouteZnodeData.builder().name("test-api").path("/test-api").rid(RID).handler(handler).code(uniqueCode).methods(List.of("POST")).desc("test").build();
        routeInfo = routeZnodeService.originalHandlerName(routeInfo);
        assertEquals(originalCode, routeInfo.getCode());
        routeInfo = routeZnodeService.uniqueHandlerName(routeInfo);
        assertEquals(uniqueCode, routeInfo.getCode());
    }
}
