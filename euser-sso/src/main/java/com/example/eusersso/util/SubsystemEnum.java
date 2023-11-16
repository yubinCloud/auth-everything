package com.example.eusersso.util;

/**
 * 枚举所有子系统
 */
public enum SubsystemEnum {

    AVUE("AVUE", "access-avue"),
    PUBLIC_API("PUBLIC_API", "access-pa");

    private final String name;  // 子系统的名字

    private final String dbAccessLabel; // 存在于数据库 euser 表中的 label

    private SubsystemEnum(String name, String dbAccessLabel) {
        this.name = name;
        this.dbAccessLabel = dbAccessLabel;
    }

    public String getName() {
        return name;
    }

    public String getDbAccessLabel() {
        return dbAccessLabel;
    }
}
