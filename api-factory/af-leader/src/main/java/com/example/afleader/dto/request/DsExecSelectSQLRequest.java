package com.example.afleader.dto.request;

import com.example.afleader.entity.DatasourceConf;
import lombok.Data;

@Data
public class DsExecSelectSQLRequest {

    private String sql;

    private DatasourceConf dsConf;
}
