package com.example.eusersso.entity;

import lombok.Data;

import java.util.List;

@Data
public class AvueRole {

    private int roleId;

    private String name;

    private List<AvuePermission> permissions;
}
