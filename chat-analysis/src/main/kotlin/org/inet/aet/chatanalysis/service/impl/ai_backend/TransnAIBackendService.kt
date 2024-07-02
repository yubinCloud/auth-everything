package org.inet.aet.chatanalysis.service.impl.ai_backend

import org.inet.aet.chatanalysis.config.AIServProperty
import org.inet.aet.chatanalysis.constant.AIServBackendFuncEnum
import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.entity.ChartConf
import org.inet.aet.chatanalysis.entity.Chat2SQLResult
import org.inet.aet.chatanalysis.entity.ChatAnalysisResult
import org.inet.aet.chatanalysis.entity.ChatAnalysisResultFactory
import org.inet.aet.chatanalysis.service.impl.DatasourceService
import org.inet.aet.chatanalysis.service.itfce.AIBackendService
import org.inet.aet.chatanalysis.typealiases.RelationalResultSet
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestClient
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.invoker.HttpServiceProxyFactory

/**
 * *****************************************************************
 * ******************** TransAI Backend API ************************
 * *****************************************************************
 */
// ***************************************************
// Transn API 的请求体 ********************************
// ***************************************************
data class NL2SQLReqJSON (
    var text: String   // user question
)
data class ChartGenReqJSON (
    var text: String,  // user question
    var data: String   // example rows
)
// ***************************************************
// Transn API 的响应格式 ******************************
// ***************************************************
data class TransnRespJSON<T> (
    var code: Int,
    var msg: String?,
    var data: T
)
data class NL2SQLRespEntityData (
    var sql: String,
    var guess: List<String>,
)
data class ChartGenRespEntityData (
    var chart: String,
    var x: String?,
    var y: String?,
    var column: String
)
// ****************************************************
// Transn 的 API Exchange 定义 *************************
// ****************************************************
@HttpExchange
interface TransnAIBackendExchange {
    @PostExchange("/api/nl_sql")
    fun nl2sql(@RequestBody body: NL2SQLReqJSON): TransnRespJSON<NL2SQLRespEntityData>

    @PostExchange("/api/chart")
    fun chartGen(@RequestBody body: ChartGenReqJSON): TransnRespJSON<ChartGenRespEntityData>
}


/**
 * Chart Type 转换
 *
 * 将接口返回的 chart type 转换为枚举的类型
 */
fun adaptChartType(chartTypeOfAI: String): ChartTypesEnum {
    return when (chartTypeOfAI.lowercase()) {
        "bar" -> ChartTypesEnum.BAR
        "none" -> ChartTypesEnum.TEXT
        else -> ChartTypesEnum.PIE
    }
}

/**
 * 传神的 AI 后端
 */
class TransnAIBackendService (aiServProperty: AIServProperty, private val datasourceService: DatasourceService): AIBackendService {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    private val transnClient: TransnAIBackendExchange

    init {
        // 初始化 transnClient
        val client = RestClient.builder()
            .baseUrl(aiServProperty.transnBackend.baseUrl)
            .build()
        val factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(client)).build()
        transnClient = factory.createClient(TransnAIBackendExchange::class.java)
    }

    override fun supportFunctions(): Set<AIServBackendFuncEnum> {
        return setOf(AIServBackendFuncEnum.NL2CHART)
    }

    override fun nl2chart(chatReq: Chat2ChartRequest): ChatAnalysisResult {

        // 调用 Transn 的 NL2SQL API，并检查调用是否出错
        val inputText = chatReq.input
        val nl2sqlReqJSON = NL2SQLReqJSON(inputText)
        val nl2sqlRespJSON = transnClient.nl2sql(nl2sqlReqJSON)
        if (nl2sqlRespJSON.code != 0) {
            val errorMsg = "Transn API 调用结果出现错误：${nl2sqlRespJSON.msg}"
            return ChatAnalysisResultFactory.fail(errorMsg)
        }
        val nl2sqlRespEntity = nl2sqlRespJSON.data
        // 执行 SQL Query，获取 result set
        val sql = nl2sqlRespEntity.sql
        val sqlExecRet = datasourceService.execQuery(chatReq.dataSourceConf, sql)
        // TODO：分析结果
        val chartType = adaptChartType(TODO())
        val chartConf = ChartConf(chartType)
        logger.info(mapOf("Q" to inputText, "TransnSQL" to transnRet.sql, "chart" to transnRet.chart).toString())
        return ChatAnalysisResultFactory.success(chartConf, transnRet.sql, null, transnRet.guess.toList())
    }

    override fun nl2sql(chatReq: Chat2ChartRequest): Chat2SQLResult {
        TODO("Not yet implemented")
    }

    /**
     * 从 result set 中取出一部分 example rows，并将其转换为 transn 所需要的 string 填入 prompt 中
     */
    private fun buildExampleRowsString(resultSet: RelationalResultSet): String {

    }

}