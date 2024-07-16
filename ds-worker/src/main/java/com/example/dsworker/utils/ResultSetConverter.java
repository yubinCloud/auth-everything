package com.example.dsworker.utils;

import com.example.dsworker.dto.response.adapter.dataease.DataAndFieldSet;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;

import static com.example.dsworker.constant.SQLResultSetFormat.ROW_FORMAT_MAP;
import static com.example.dsworker.constant.SQLResultSetFormat.ROW_FORMAT_ARRAY;

public class ResultSetConverter {

    /**
     * 将 ResultSet 结果转为 List
     * @param rs
     * @return
     * @throws SQLException
     */
    static public List<Map<String, Object>> toMapList(ResultSet rs) throws SQLException {
        return toMapList(rs, null, null);
    }

    static public List<Map<String, Object>> toMapList(ResultSet rs, Integer limit, Integer offset) throws SQLException {
        return toRowList(rs, limit, offset, ROW_FORMAT_MAP).stream().map(row -> (Map<String, Object>) row).toList();
    }

    static public List<String[]> toArrayList(ResultSet rs, Integer limit, Integer offset) throws SQLException {
        return toRowList(rs, limit, offset, ROW_FORMAT_ARRAY).stream().map(row -> (String[]) row).toList();
    }

    static public List<DataAndFieldSet.TableField> toFieldList(ResultSet rs) throws SQLException {
        List<DataAndFieldSet.TableField> fieldList = new ArrayList<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        for (int j = 0; j < columnCount; j++) {
            String colName = metaData.getColumnName(j + 1);
            String colLabel = StringUtils.isNotEmpty(metaData.getColumnLabel(j + 1)) ? metaData.getColumnLabel(j + 1): colName;
            String colType = metaData.getColumnTypeName(j + 1);
            var field = new DataAndFieldSet.TableField();
            field.setFieldName(colLabel);
            field.setRemarks(colLabel);
            field.setFieldType(colType);
            field.setFieldSize(metaData.getColumnDisplaySize(j + 1));
            if (colType.equalsIgnoreCase("LONG")) {
                field.setFieldSize(65533);
            }
            if (StringUtils.isNotEmpty(colType) && colType.toLowerCase().contains("date") && field.getFieldSize() < 50) {
                field.setFieldSize(50);
            }
            fieldList.add(field);
        }
        return fieldList;
    }

    static private List<Object> toRowList(ResultSet rs, Integer limit, Integer offset, int rowType) throws SQLException {
        if (rs == null || (limit != null && limit <= 0)) {
            return Collections.emptyList();
        }
        List<Object> list = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();
        int idx = 0;
        while (rs.next() && (limit == null || limit > 0)) {
            if (offset != null && offset >= 0 && idx < offset) {
                ++idx;
                continue;
            }
            if (limit != null) {
                limit--;
            }
            Object row = null;
            switch (rowType) {
                case ROW_FORMAT_MAP:
                    row = toMapRow(rs, md, colCount);
                    break;
                case ROW_FORMAT_ARRAY:
                    row = toArrayRow(rs, md, colCount);
                    break;
                default:
                    break;
            }
            list.add(row);
        }
        return list;
    }

    static private Map<String, Object> toMapRow(ResultSet rs, ResultSetMetaData metaData, int colCount) throws SQLException {
        Map<String, Object> rowData = new HashMap<>();
        for (int i = 1; i <= colCount; i++) {
            rowData.put(metaData.getColumnLabel(i), rs.getObject(i));
        }
        return rowData;
    }

    static private String[] toArrayRow(ResultSet rs, ResultSetMetaData metaData, int colCount) throws SQLException {
        var row = new String[colCount];
        for (int j = 0; j < colCount; j++) {
            int colType = metaData.getColumnType(j + 1);
            switch (colType) {
                case Types.DATE:
                    if (rs.getDate(j + 1) != null) {
                        row[j] = rs.getDate(j + 1).toString();
                    }
                    break;
                case Types.BOOLEAN:
                    row[j] = rs.getBoolean(j + 1)? "1": "0";
                    break;
                default:
                    row[j] = rs.getString(j + 1);
                    break;
            }
        }
        return row;
    }
}
