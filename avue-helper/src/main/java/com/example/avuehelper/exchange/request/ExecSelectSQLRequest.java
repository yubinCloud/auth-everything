package com.example.avuehelper.exchange.request;

import com.example.avuehelper.entity.DataSourceConf;
import com.example.avuehelper.entity.SQLSlot;
import lombok.Data;

import java.util.Map;

@Data
public class ExecSelectSQLRequest {

    private DataSourceConf dataSourceConf;

    private String sql;

    private Map<String, SQLSlot> slots;

    private Integer queryLimit;

    private Integer queryOffset;
}
