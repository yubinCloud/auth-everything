package com.example.ssoauth.mapstructutil;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;

import java.util.List;

public class CommonConverterUtil {

    public static List<String> jsonArrayToStrList(JSONArray jsonArray) {
        return jsonArray.toList(String.class);
    }

    public static String strListToJsonStr(List<String> list) {
        return JSONUtil.parseArray(list).toString();
    }
}
