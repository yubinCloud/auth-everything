package com.example.avuehelper.entity;

import lombok.Data;

@Data
public class DataSourceConf {

    private String driverClass;

    private String url;

    private String username;

    private String password;
}
