package com.example.afleader.exchange.request.afworker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class AddRouteForSQLReq {

    private String path;

    private String name;

    @JsonProperty("route_id")
    private String routeId;

    private String sql;

    @JsonProperty("ds_conf")
    private String dsConf;
}
