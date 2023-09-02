package com.example.ssoauth.entity;

import lombok.Data;

import java.util.List;

@Data
public class Role {

    private Integer id;

    private String name;

    private List<String> permissionList;

}
