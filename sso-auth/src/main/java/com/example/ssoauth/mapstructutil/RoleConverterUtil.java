package com.example.ssoauth.mapstructutil;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RoleConverterUtil {

    @Named("jsonArrayToStrList")
    public List<String> jsonArrayToStrList(JSONArray jsonArray) {
        return CommonConverterUtil.jsonArrayToStrList(jsonArray);
    }

    @Named("strListToJsonArray")
    public JSONArray strListToJsonArray(List<String> strList) {
        return CommonConverterUtil.strListToJsonArray(strList);
    }

}
