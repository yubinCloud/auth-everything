package com.example.dsworker.entity;

import lombok.Data;

import java.util.List;

@Data
public class DBSchema {

    @Data
    static public class DBTable {
        private String name;
        private String remark;
        private List<DBColumn> columns;
        private List<String> primaryKeys;
    }

    @Data
    static public class DBColumn {
        private String name;
        private String typ;
        private int nullable;
        private String remark;
    }

    private String dbName;
    private String dbProductName;
    private String dbProductVersion;
    private String dbDriverName;
    private List<DBTable> tables;

}
