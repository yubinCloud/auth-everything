package com.example.afleader.exchange.request.afworker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class HandlerCodeForSQLReq {

    @JsonProperty("route_id")
    private String routeId;

    private String sql;

    @JsonProperty("ds_conf")
    private Map<String, String> dsConf;
}
