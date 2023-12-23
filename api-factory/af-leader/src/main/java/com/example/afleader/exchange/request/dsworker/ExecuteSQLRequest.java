package com.example.afleader.exchange.request.dsworker;

import com.example.afleader.entity.DatasourceConf;
import lombok.Data;

@Data
public class ExecuteSQLRequest {

    private DatasourceConf datasourceConf;

    private String sql;
}
