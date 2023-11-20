package com.example.dsworker.service;

import com.example.dsworker.dto.request.ExecuteSQLRequest;
import com.example.dsworker.dto.request.SQLSlot;
import com.example.dsworker.exception.InputSlotException;
import com.example.dsworker.utils.ResultSetConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ExecuteService {

    private final DataSourceService dataSourceService;

    private static final Pattern SQL_SLOT_PATTERN = Pattern.compile("#\\{.+?}");

    public List<Map<String, Object>> execQueryWithoutSlots(ExecuteSQLRequest body) throws SQLException {
        List<Map<String, Object>> list;
        try (
                Connection conn = dataSourceService.getConnection(body.getDataSourceConf());
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery(body.getSql())
        ) {
            list = ResultSetConverter.toList(rs);
            conn.commit();
        }
        return list;
    }

    public List<Map<String, Object>> execQueryWithSlots(ExecuteSQLRequest body) throws SQLException {
        List<SQLSlot> slots = new ArrayList<>();
        String slottedSQL = fillSlots(body.getSql(), body.getSlots(), slots);
        List<Map<String, Object>> list;
        try (
                Connection conn = dataSourceService.getConnection(body.getDataSourceConf());
                PreparedStatement preparedStatement = createPreparedStatement(conn, slottedSQL, slots);
                ResultSet rs = preparedStatement.executeQuery();
        ) {
            list = ResultSetConverter.toList(rs);
            conn.commit();
        }
        return list;
    }

    public int execUpdateWithoutSlots(ExecuteSQLRequest body) throws SQLException {
        int count = 0;
        try (
                Connection conn = dataSourceService.getConnection(body.getDataSourceConf());
                Statement statement = conn.createStatement()
        ) {
            count = statement.executeUpdate(body.getSql());
            conn.commit();
        }
        return count;
    }

    private PreparedStatement createPreparedStatement(Connection conn, String SQL, List<SQLSlot> slots) throws SQLException {
        PreparedStatement preparedStatement = conn.prepareStatement(SQL);
        for (int i = 0; i < slots.size(); i++) {
            var slot = slots.get(i);
            String slotType = slot.getType();
            Object slotValue = slot.getValue();
            switch (slotType.toUpperCase()) {
                case "STRING" -> preparedStatement.setString(i + 1, (String) slotValue);
                case "INTEGER", "INT" -> preparedStatement.setInt(i + 1, (Integer) slotValue);
                default -> throw new InputSlotException();
            }
        }
        return preparedStatement;
    }


    private String fillSlots(String sql, Map<String, SQLSlot> slots, List<SQLSlot> outSlots) {
        StringBuilder sb = new StringBuilder();
        Matcher m = SQL_SLOT_PATTERN.matcher(sql);
        while (m.find()) {
            String slot = m.group();
            String slotKey = slot.substring(2, slot.length() - 1);
            SQLSlot sqlSlot = slots.get(slotKey);
            if (Objects.isNull(sqlSlot)) {
                throw new InputSlotException();
            }
            outSlots.add(sqlSlot);
            m.appendReplacement(sb, "?");
        }
        m.appendTail(sb);
        System.out.println(outSlots);
        return sb.toString();
    }

}
