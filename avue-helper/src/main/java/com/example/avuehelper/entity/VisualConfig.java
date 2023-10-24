package com.example.avuehelper.entity;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class VisualConfig {

    private long visualId;

    private JSONObject detail;

    private JSONArray component;
}
