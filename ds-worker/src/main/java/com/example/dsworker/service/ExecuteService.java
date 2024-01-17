package com.example.dsworker.service;

import com.example.dsworker.dto.request.ExecuteMultiSQLRequest;
import com.example.dsworker.dto.request.ExecuteSQLRequest;
import com.example.dsworker.dto.request.SQLSlot;
import com.example.dsworker.dto.response.ExecuteMultiSQLResponse;
import com.example.dsworker.dto.response.ExecuteSQLResult;
import com.example.dsworker.exception.InputSlotException;
import com.example.dsworker.exception.SQLExecuteException;
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
//            conn.commit();
        }
        return list;
    }

    public List<Map<String, Object>> execQueryWithSlots(ExecuteSQLRequest body) throws SQLException {
        List<SQLSlot> slots = new ArrayList<>();
        String slottedSQL = fillSlots(body.getSql(), body.getSlots(), slots);
        System.out.println(slottedSQL);
        List<Map<String, Object>> list;
        try (
                Connection conn = dataSourceService.getConnection(body.getDataSourceConf());
                PreparedStatement preparedStatement = createPreparedStatement(conn, slottedSQL, slots);
                ResultSet rs = preparedStatement.executeQuery()
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

    public ExecuteMultiSQLResponse execMultiSQL(ExecuteMultiSQLRequest body) {
        List<ExecuteSQLResult> results;
        var multi = body.getMulti();
        try (Connection conn = dataSourceService.getConnection(body.getDataSourceConf())) {
            results = multi.stream().map(sqlItem ->
                 execSQL(conn, sqlItem.getSql(), sqlItem.getSlots())
            ).toList();
        } catch (SQLException e) {
            throw new SQLExecuteException(e.getMessage());
        }
        var resp = new ExecuteMultiSQLResponse();
        resp.setExecResults(results);
        return resp;
    }

    /**
     * 执行一条 SQL 语句，并获取结果
     * @param conn
     * @param SQL
     * @param slots
     * @return
     */
    private ExecuteSQLResult execSQL(Connection conn, String SQL, List<SQLSlot> slots) {
        var execResult = new ExecuteSQLResult();
        if (Objects.isNull(slots)) {
            slots = Collections.emptyList();
        }
        try (var statement = createPreparedStatement(conn, SQL, slots)) {
            boolean isQuery = statement.execute();
            if (isQuery) {
                execResult.setQuery(true);
                execResult.setRows(ResultSetConverter.toList(statement.getResultSet()));
            } else {
                execResult.setQuery(false);
                execResult.setCount(statement.getUpdateCount());
            }
        } catch (SQLException e) {
            throw new SQLExecuteException(e.getMessage());
        }
        return execResult;
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
                case  "DOUBLE", "FLOAT", "NUMBER" -> preparedStatement.setDouble(i + 1, (Double) slotValue);
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
            String slotKey = slot.substring(2, slot.length() - 1).split(",")[0];
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
