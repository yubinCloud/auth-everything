package com.example.avuehelper.service.rpc;

import com.example.avuehelper.constant.BzExceptionEnum;
import com.example.avuehelper.entity.DataSourceConf;
import com.example.avuehelper.entity.SQLSlot;
import com.example.avuehelper.exception.BzExceptionFactory;
import com.example.avuehelper.exchange.api.DsWorkerExchange;
import com.example.avuehelper.exchange.request.ExecSelectSQLRequest;
import com.example.avuehelper.exchange.response.DsWorkerRespJSON;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

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
        DsWorkerRespJSON<List<Map<String, Object>>> dsWorkerResp;
        try {
            dsWorkerResp = dsWorkerExchange.execSelectSQL(requestBody);
        } catch (WebClientResponseException.ServiceUnavailable e) {
            throw BzExceptionFactory.make(BzExceptionEnum.DS_WORKER_ERROR, "ds-worker 无法连接");
        } catch (WebClientResponseException e) {
            throw BzExceptionFactory.make(BzExceptionEnum.DS_WORKER_ERROR, "ds-worker 出现错误：" + e.getLocalizedMessage());
        }
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
