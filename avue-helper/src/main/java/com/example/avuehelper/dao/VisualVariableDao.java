package com.example.avuehelper.dao;

import cn.hutool.json.JSONObject;
import lombok.Data;

@Data
public class VisualVariableDao {

    private long id;

    private JSONObject variable;
}
