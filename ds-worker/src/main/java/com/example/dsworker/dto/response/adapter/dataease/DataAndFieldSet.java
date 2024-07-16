package com.example.dsworker.dto.response.adapter.dataease;

import lombok.Data;

import java.util.List;

@Data
public class DataAndFieldSet {

    @Data
    static public class TableField {
        private String fieldName;

        private String remarks;

        private String fieldType;

        private int fieldSize;
    }

    private List<String[]> dataList;

    private List<TableField> fieldList;

}
