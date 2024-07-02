package com.example.dsworker.utils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class ResultSetConverter {

    /**
     * 将 ResultSet 结果转为 List
     * @param rs
     * @return
     * @throws SQLException
     */
    static public List<Map<String, Object>> toList(ResultSet rs) throws SQLException {
        if (rs == null) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> list = new ArrayList<>();
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();
        while (rs.next()) {
            Map<String, Object> rowData = new HashMap<>();
            for (int i = 1; i <= colCount; i++) {
                rowData.put(md.getColumnName(i), rs.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }

    static public List<Map<String, Object>> toList(ResultSet rs, Integer limit, Integer offset) throws SQLException {
        if (rs == null || (limit != null && limit <= 0)) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = new ArrayList<>();
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
            Map<String, Object> rowData = new HashMap<>();
            for (int i = 1; i <= colCount; i++) {
                rowData.put(md.getColumnLabel(i), rs.getObject(i));
            }
            list.add(rowData);
        }
        return list;
    }
}
