package com.example.eusersso.util;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import org.springframework.stereotype.Component;

@Component
public class PasswordEncoder {

    public String encode(String rawPwd) {
        if (StrUtil.isBlank(rawPwd)) return null;
        return DigestUtil.sha256Hex(rawPwd);
    }

    public boolean match(String pwdInParam, String pwdInDb) {
        if (StrUtil.isBlank(pwdInParam)) {
            return StrUtil.isBlank(pwdInDb);
        }
        return DigestUtil.sha256Hex(pwdInParam).equals(pwdInDb);
    }
}
