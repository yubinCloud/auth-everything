package com.example.ssoauth.dao.param;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PermissionInsertParam {

    private String username;

    private String permissionList;
}
