package com.example.eusergateway.exchange.response.eusersso;

import lombok.Data;

import java.util.List;

@Data
public class PersonalPermissionInfo {

    List<AvuePermission> avuePermissions;

    List<PublicApiPermission> publicApiPermissions;
}
