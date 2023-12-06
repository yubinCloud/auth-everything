package com.example.afleader.exception;

public class DynamicRouteCreateException extends BaseBzException {

    public DynamicRouteCreateException(final String message) {
        super(message);
    }

    public DynamicRouteCreateException() {
        super("动态路由创建失败");
    }
}
