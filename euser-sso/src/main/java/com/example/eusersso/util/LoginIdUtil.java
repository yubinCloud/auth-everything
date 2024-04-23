package com.example.eusersso.util;

import org.springframework.stereotype.Component;

@Component
public class LoginIdUtil {
    public String[] splitLoginId(String LoginId) {
        return LoginId.split(",");
    }

    public String appendLoginId( Integer tenantId,String username) {
        return tenantId + "," + username;
    }
    public String appendLoginId( String tenantId,String username) {
        return tenantId + "," + username;
    }
}
