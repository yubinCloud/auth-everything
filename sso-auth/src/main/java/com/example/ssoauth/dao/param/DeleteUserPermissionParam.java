package com.example.ssoauth.dao.param;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteUserPermissionParam {

    private String username;

    private String permission;
}
