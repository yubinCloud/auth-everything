package com.example.ssoauth.mapstructutil;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.ssoauth.util.PasswordEncoder;
import com.example.ssoauth.util.PermissionUtil;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserConverterUtil {

    private final PasswordEncoder passwordEncoder;

    @Named("passwordEncode")
    public String passwordEncode(String s) {
        return passwordEncoder.encode(s);
    }

    @Named("jsonArrayToStrList")
    public List<String> jsonArrayToStrList(JSONArray jsonArray) {
        return jsonArray.toList(String.class);
    }

    @Named("strListToJsonArray")
    public JSONArray strListToJsonArray(List<String> list) {
        return JSONUtil.parseArray(list);
    }

    @Named("strListToJsonStr")
    public String strListToJsonStr(List<String> list) {
        return JSONUtil.parseArray(list).toString();
    }
}
