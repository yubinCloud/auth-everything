package com.example.eusersso.dao.param;

import lombok.Data;

@Data
public class AvueRoleSelectCond {

    private Integer roleId;

    private String name;

    private int pageNum;

    private int pageSize;
}
