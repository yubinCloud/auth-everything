package com.example.avuehelper.dto.response;

import lombok.Data;

/**
 * 数据库的一个 table
 */
@Data
public class DbTableItem {

    private String name;

    // 数据库的注释字段
    private String remarks;

    // JDBC 接口返回的 `TABLE_TYPE` 字段
    private String tableType;
}
