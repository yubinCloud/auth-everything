package com.example.afleader.exchange.response.afworker;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HandlerCodeForSQLResp {

    @JsonProperty("handler_code")
    private String handlerCode;

    @JsonProperty("handler_name")
    private String handlerName;
}
