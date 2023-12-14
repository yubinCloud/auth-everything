package com.example.afleader.exception;

public class DynamicRouteOpsException extends BaseBzException {

    public DynamicRouteOpsException(final String message) {
        super(message);
    }

    public DynamicRouteOpsException() {
        super("动态路由创建失败");
    }
}
