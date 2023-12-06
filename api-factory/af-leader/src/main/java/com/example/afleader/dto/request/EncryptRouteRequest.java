package com.example.afleader.dto.request;

import lombok.Data;

@Data
public class EncryptRouteRequest {

    private String path;

    private boolean encrypt;

}
