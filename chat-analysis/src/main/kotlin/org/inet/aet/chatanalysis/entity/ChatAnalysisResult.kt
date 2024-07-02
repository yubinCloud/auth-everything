package org.inet.aet.chatanalysis.entity

import org.inet.aet.chatanalysis.constant.ChartTypesEnum

data class ChartConf (
    var chartType: ChartTypesEnum,   // 分析图的类型
)

data class ChatAnalysisResult (
    var success: Boolean,  // 是否生成成功
    var errorMsg: String,  // 生成错误时的错误消息
    var chartConf: ChartConf,  // 分析图的相关配置
    var sql: String,  // 生成的 SQL,
    var sqlQueryExecResult: SQLQueryExecResult?,  // SQL Query 执行后的结果。如果为 null 且 success 为 true，则交由上层服务来完成 SQL 的查询
    var nextMaybe: List<String>,  // 接下来用户问的可能问题
)

class ChatAnalysisResultFactory {

    companion object {
        @JvmStatic fun fail(errorMsg: String): ChatAnalysisResult {
            return ChatAnalysisResult(false, errorMsg, ChartConf(ChartTypesEnum.BAR), "", null, listOf())
        }

        @JvmStatic fun success(chartConf: ChartConf, sql: String, sqlQueryExecResult: SQLQueryExecResult?, nextMaybe: List<String>): ChatAnalysisResult {
            return ChatAnalysisResult(true, "", chartConf, sql, sqlQueryExecResult, nextMaybe)
        }
    }
}