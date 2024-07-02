package org.inet.aet.chatanalysis.exchange.request

import org.inet.aet.chatanalysis.entity.DataSourceConf


data class ExecSelectSQLRequest (
    var dataSourceConf: DataSourceConf,
    var sql: String,
    var slots: Map<String, Any?>,
    var queryLimit: Int?,
    var queryOffset: Int?
)

data class DBSchemaRequest(
    var dataSourceConf: DataSourceConf,
)