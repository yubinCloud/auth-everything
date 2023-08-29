package com.example.ssoauth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeleteUserReq {

    @NotBlank
    private String username;
}
