package com.example.ssoauth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteTenantReq {
    @NotBlank
    private String name;
}
