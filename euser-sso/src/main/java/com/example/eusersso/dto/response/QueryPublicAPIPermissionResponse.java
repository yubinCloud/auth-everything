package com.example.eusersso.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class QueryPublicAPIPermissionResponse {

    private List<String> routes;
}
