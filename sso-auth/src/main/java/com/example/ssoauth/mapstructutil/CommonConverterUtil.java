package com.example.ssoauth.mapstructutil;

import cn.hutool.json.JSONArray;

import java.util.List;

public class CommonConverterUtil {

    public static List<String> jsonArrayToStrList(JSONArray jsonArray) {
        return jsonArray.toList(String.class);
    }
}
