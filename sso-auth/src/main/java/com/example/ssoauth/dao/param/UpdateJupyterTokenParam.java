package com.example.ssoauth.dao.param;

import lombok.Data;

@Data
public class UpdateJupyterTokenParam {
    private String username;
    private String jupyterToken;
}
