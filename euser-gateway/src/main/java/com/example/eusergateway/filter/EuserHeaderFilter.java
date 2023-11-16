package com.example.eusergateway.filter;

import cn.dev33.satoken.stp.StpUtil;
import com.alibaba.cloud.commons.lang.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * 用来给每个 Request 的 Header 增加一行 Euser: xxx，方便后面的服务获得 username
 */
@Configuration
@Slf4j
@Order(0)
public class EuserHeaderFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        var request = exchange.getRequest();
        String token = request.getHeaders().getFirst("Eauth");
        if (StringUtils.isBlank(token)) {
            return chain.filter(exchange);
        }
        String loginId = (String) StpUtil.getLoginIdByToken(token);
        if (Objects.isNull(loginId)) {
            return chain.filter(exchange);
        }
        request = request.mutate().header("X-Euser", loginId).build();
        return chain.filter(exchange.mutate().request(request).build());
    }
}
