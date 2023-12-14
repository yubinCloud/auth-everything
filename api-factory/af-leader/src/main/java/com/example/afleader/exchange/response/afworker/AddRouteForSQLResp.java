package com.example.afleader.exchange.response.afworker;

import lombok.Data;

import java.util.List;

@Data
public class AddRouteForSQLResp {

    private String rid;

    private String name;

    private String path;

    private List<String> methods;

    private String code;
}
