package com.example.eusergateway.service;

import org.springframework.stereotype.Service;

@Service
public class PermissionFactory {

    /**
     * Avue 大屏的 visualId 的权限
     */
    public static final String PERMISSION_PREFIX_VISUAL_ID = "avi:"; // AvueVisualId

    /**
     * Avue 大屏中的组件的权限
     */
    public static final String PERMISSION_PREFIX_VISUAL_COMPONENT_ID = "avc:"; // AvueVisualComponent

    /**
     * Public API 的 apiId 的权限
     */
    public static final String PERMISSION_PREFIX_PUBLIC_API_ID = "pai:"; // PublicApiID

    public String createVisualIdPermission(long visualId) {
        return PERMISSION_PREFIX_VISUAL_ID + visualId;
    }

    public String createVisualComponentPermission(String componentId) {
        return PERMISSION_PREFIX_VISUAL_COMPONENT_ID + componentId;
    }

    public String createPublicApiIdPermission(String apiId) {
        return PERMISSION_PREFIX_PUBLIC_API_ID + apiId;
    }
}
