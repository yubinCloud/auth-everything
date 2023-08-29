package com.example.ssoauth.util;

import java.util.List;

public class PermissionUtil {
    public static String[] splitPermission(String perm) {
        return perm.split("\\|");
    }
}
