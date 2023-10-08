package com.example.afencryptiongateway.exchange.response;

import lombok.Data;

@Data
public class FetchSecretKeyResponse {
    private String secretKey;
    private String alg;
}
