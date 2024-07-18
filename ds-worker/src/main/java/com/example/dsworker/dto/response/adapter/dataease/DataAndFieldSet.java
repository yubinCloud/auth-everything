package com.example.dsworker.dto.response.adapter.dataease;

import lombok.Data;

import java.util.List;

@Data
public class DataAndFieldSet {

    private List<String[]> dataList;

    private List<TableField> fieldList;

}
