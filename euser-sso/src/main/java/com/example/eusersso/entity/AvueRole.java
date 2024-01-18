package com.example.eusersso.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class AvueRole {

    private int roleId;

    private String name;

    private List<AvuePermission> permissions;

    private List<Long> sysHomes;
}
