package org.inet.aet.chatanalysis.service.impl

import org.inet.aet.chatanalysis.constant.MicroServiceErrorCode
import org.inet.aet.chatanalysis.entity.DBSchema
import org.inet.aet.chatanalysis.entity.DataSourceConf
import org.inet.aet.chatanalysis.exception.BaseBzException
import org.inet.aet.chatanalysis.exchange.api.DsWorkerExchange
import org.inet.aet.chatanalysis.exchange.request.DBSchemaRequest
import org.inet.aet.chatanalysis.exchange.request.ExecSelectSQLRequest
import org.inet.aet.chatanalysis.entity.SQLQueryExecResult
import org.springframework.stereotype.Service

@Service
class DatasourceService (private val dsWorkerExchange: DsWorkerExchange) {

    fun getDBSchema(dsConf: DataSourceConf): DBSchema {
        val requestBody = DBSchemaRequest(dsConf)
        val respBody = dsWorkerExchange.getDBSchema(requestBody)
        if (respBody.code != 0 || respBody.data == null) {
            throw BaseBzException("获取 DB schema 信息失败")
        }
        return respBody.data!!
    }

    fun execQuery(dsConf: DataSourceConf, sql: String, slots: Map<String, Any?> = emptyMap()): SQLQueryExecResult {
        val requestBody = ExecSelectSQLRequest(dsConf, sql, slots, null, null)
        val resp = dsWorkerExchange.executeSelectSQL(requestBody)
        var sqlSuccess = true
        if (resp.code == MicroServiceErrorCode.DW_SQL_EXEC_ERROR) {  // if SQL 执行失败
            sqlSuccess = false
        }
        val errorReason = if (sqlSuccess || resp.msg == null) "" else resp.msg!!
        val records = if (resp.data != null) resp.data!! else emptyList()
        return SQLQueryExecResult(sqlSuccess, errorReason, records)
    }
}