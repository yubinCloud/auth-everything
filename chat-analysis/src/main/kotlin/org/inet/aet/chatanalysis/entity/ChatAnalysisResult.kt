package org.inet.aet.chatanalysis.entity

import org.inet.aet.chatanalysis.constant.ChartTypesEnum

data class ChartConf (
    var chartType: ChartTypesEnum,   // 分析图的类型
)

data class ChatAnalysisResult (
    var chartConf: ChartConf,  // 分析图的相关配置
    var sql: String,  // 生成的 SQL,
    var sqlQueryExecResult: SQLQueryExecResult?,  // SQL Query 执行后的结果。如果为 null，则交由上层服务来完成 SQL 的查询
    var nextMaybe: List<String>,  // 接下来用户问的可能问题
) {
    companion object Factory {
        fun create (chartType: ChartTypesEnum, sql: String, sqlExecResult: SQLQueryExecResult?, nextMaybe: List<String>): ChatAnalysisResult {
            return ChatAnalysisResult(ChartConf(chartType), sql, sqlExecResult, nextMaybe)
        }
    }
}