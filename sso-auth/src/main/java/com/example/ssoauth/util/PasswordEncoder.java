package com.example.ssoauth.util;


import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    public String encode(String rawPwd) {
        if (rawPwd == null)
            return null;
        return DigestUtil.md5Hex(rawPwd);
    }

    public boolean match(String rawPwd, String encryptedPwd) {
        if (rawPwd == null) {
            return encryptedPwd == null;
        }
        return DigestUtil.md5Hex(rawPwd).equals(encryptedPwd);
    }
}
