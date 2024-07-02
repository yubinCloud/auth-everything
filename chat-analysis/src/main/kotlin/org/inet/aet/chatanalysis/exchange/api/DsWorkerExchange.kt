package org.inet.aet.chatanalysis.exchange.api

import org.inet.aet.chatanalysis.entity.DBSchema
import org.inet.aet.chatanalysis.exchange.request.DBSchemaRequest
import org.inet.aet.chatanalysis.exchange.request.ExecSelectSQLRequest
import org.inet.aet.chatanalysis.exchange.response.DsWorkerRespJSON
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange

@HttpExchange("/ds-worker")
interface DsWorkerExchange {

    @PostExchange("/exec/select")
    fun executeSelectSQL(@RequestBody body: ExecSelectSQLRequest): DsWorkerRespJSON<List<Map<String, String>>?>

    @PostExchange("/meta/db-schema")
    fun getDBSchema(@RequestBody body: DBSchemaRequest): DsWorkerRespJSON<DBSchema>
}