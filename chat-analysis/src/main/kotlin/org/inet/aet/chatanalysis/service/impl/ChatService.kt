package org.inet.aet.chatanalysis.service.impl


import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.dto.response.Chat2ChartResponse
import org.inet.aet.chatanalysis.service.itfce.AIBackendService
import org.springframework.stereotype.Service

@Service
class ChatService (
    private val aiBackendService: AIBackendService,
    private val datasourceService: DatasourceService,
    private val chartDataFormatProcessService: ChartDataFormatProcessService
) {

    fun supportFunctions(): List<String> {
        return aiBackendService.supportFunctions().stream().map { it.nm }.toList()
    }

    /**
     * 通过对话生成 chart
     */
    fun chat2chart(chatGenReq: Chat2ChartRequest): Chat2ChartResponse {
        val analysisResult = aiBackendService.nl2chart(chatGenReq)
        val sql = analysisResult.sql
        // 检查是否需要执行 SQL
        if (analysisResult.sqlQueryExecResult == null && analysisResult.success) {
            analysisResult.sqlQueryExecResult = datasourceService.execQuery(chatGenReq.dataSourceConf, sql)
        }
        // 根据 nl2chart 结果，组装本次 request 的 resp
        val chartContent = if (analysisResult.success) chartDataFormatProcessService.processRelationalResultSet(analysisResult.chartConf.chartType, analysisResult.sqlQueryExecResult!!.resultSet) else null
        val errorReason = if (!analysisResult.success) analysisResult.errorMsg else analysisResult.sqlQueryExecResult!!.errorReason
        return Chat2ChartResponse(
            outputText = "",
            chartType = analysisResult.chartConf.chartType.nm,
            sql = sql,
            success = analysisResult.success && analysisResult.sqlQueryExecResult!!.success,
            errorReason = errorReason,
            chartContent = chartContent,
            nextMaybe = analysisResult.nextMaybe
        )
    }
}