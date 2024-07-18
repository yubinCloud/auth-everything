package com.example.avuehelper.service.rpc;

import com.example.avuehelper.entity.DataSourceConf;
import com.example.avuehelper.entity.SQLSlot;
import com.example.avuehelper.exchange.api.DsWorkerExchange;
import com.example.avuehelper.exchange.request.ExecSelectSQLRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DsWorkerRpcService {

    private final DsWorkerExchange dsWorkerExchange;

    /**
     * 通过远程调用 ds-worker 来执行 SQL
     * @param dsConf
     * @param sql
     * @param slots
     * @param queryLimit
     * @param queryOffset
     * @return
     * @throws SQLException
     */
    public List<Map<String, Object>> execSelectSQL(DataSourceConf dsConf, String sql, Map<String, SQLSlot> slots, Integer queryLimit, Integer queryOffset) throws SQLException {
        // 填充 request
        var requestBody = new ExecSelectSQLRequest();
        requestBody.setDataSourceConf(dsConf);
        requestBody.setSql(sql);
        requestBody.setSlots(slots);
        requestBody.setQueryLimit(queryLimit);
        requestBody.setQueryOffset(queryOffset);
        // 发起远程调用
        var dsWorkerResp = dsWorkerExchange.execSelectSQL(requestBody);
        // 获取 resp
        if (dsWorkerResp.getCode() != 0) {
            throw new SQLException(dsWorkerResp.getMsg());
        }
        return dsWorkerResp.getData();
    }

    public List<Map<String, Object>> execSelectSQL(DataSourceConf dsConf, String sql) throws SQLException {
        return execSelectSQL(dsConf, sql, Collections.emptyMap(), null, null);
    }
}
