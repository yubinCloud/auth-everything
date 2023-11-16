package com.example.eusergateway.exchange.response;

import lombok.Data;

import java.util.List;

@Data
public class AvuePermission {

    private long visualId;

    private List<String> whitelist;
}
