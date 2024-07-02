package org.inet.aet.chatanalysis.entity

import org.inet.aet.chatanalysis.typealiases.RelationalResultSet

/**
 * （使用 ds-worker）执行 SQL Query 之后的结果
 */
data class SQLQueryExecResult (
    var success: Boolean,  // SQL 执行是否成功
    var errorReason: String,  // SQL 执行错误时的错误原因
    var resultSet: RelationalResultSet  // SQL 执行的结果集
)