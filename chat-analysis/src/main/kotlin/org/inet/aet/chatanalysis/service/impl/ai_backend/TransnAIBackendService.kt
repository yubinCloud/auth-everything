package org.inet.aet.chatanalysis.service.impl.ai_backend

import org.inet.aet.chatanalysis.config.AIServProperty
import org.inet.aet.chatanalysis.constant.AIServBackendFuncEnum
import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.entity.*
import org.inet.aet.chatanalysis.exception.Err
import org.inet.aet.chatanalysis.exception.LogicalException
import org.inet.aet.chatanalysis.service.impl.DatasourceService
import org.inet.aet.chatanalysis.service.itfce.AIBackendService
import org.inet.aet.chatanalysis.typealiases.RelationalResultSet
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestClientException
import org.springframework.web.client.support.RestClientAdapter
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import org.springframework.web.service.invoker.HttpServiceProxyFactory
import java.util.StringJoiner

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
 * 传神的 AI 后端
 */
@Service
@Lazy
class TransnAIBackendService (aiServProperty: AIServProperty, private val datasourceService: DatasourceService): AIBackendService {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        // Hyper-parameters
        private const val EXAMPLE_ROWS_NUMBER = 3  // 给 transn AI 后端的 rows 示例的个数
    }

    // 与 transn 服务进行交互的 client，其初始化在 init() 中
    private val transnClient: TransnAIBackendExchange = run {
        val restClient = RestClient.builder()
            .baseUrl(aiServProperty.transnBackend.baseUrl)
            .build()
        val factory = HttpServiceProxyFactory.builderFor(RestClientAdapter.create(restClient)).build()
        factory.createClient(TransnAIBackendExchange::class.java)
    }

    override fun supportFunctions(): Set<AIServBackendFuncEnum> {
        return setOf(AIServBackendFuncEnum.NL2CHART)
    }

    override fun nl2chart(chatReq: Chat2ChartRequest): ChatAnalysisResult {
        // 调用 Transn 的 NL2SQL API，并检查调用是否出错
        val inputText = chatReq.input
        val nl2sqlReqJSON = NL2SQLReqJSON(inputText)
        val nl2sqlRespJSON = try {
            transnClient.nl2sql(nl2sqlReqJSON)
        } catch (e: RestClientException) {
            val errorMsg = "Transn API（nl2sql）状态码错误：${e.message}"
            throw LogicalException.create(Err.AI_BACKEND_CALL_ERROR, errorMsg)
        }
        if (nl2sqlRespJSON.code != 0) {
            val errorMsg = "Transn API（nl2sql）调用结果出现错误：${nl2sqlRespJSON.msg}"
            throw LogicalException.create(Err.AI_BACKEND_CALL_ERROR, errorMsg)
        }
        val nl2sqlRespEntity = nl2sqlRespJSON.data
        // 执行 SQL Query，获取 result set
        val sql = nl2sqlRespEntity.sql
        val sqlExecRet = datasourceService.execQuery(chatReq.dataSourceConf, sql)
        if (!sqlExecRet.success) {
            throw LogicalException.create(Err.SQL_EXEC_ERROR, sqlExecRet.errorReason)
        }
        val resultSet = sqlExecRet.resultSet
        // 如果 result set 为空，则表名 SQL 没有执行结果，直接返回错误
        if (resultSet.isEmpty()) {
            throw LogicalException.create(Err.EMPTY_RESULT_SET)
        }
        // 如果 result set 仅有一行，那 chart type 直接定为 ROW_TABLE 类型
        if (resultSet.size == 1) {
            return ChatAnalysisResult.create(ChartTypesEnum.RAW_TABLE, sql, sqlExecRet, nl2sqlRespEntity.guess)
        }
        // 从 result set 中抽取一部分样本 rows，拼装为 String 给 transn 作为 prompt
        val exampleRows = buildExampleRowsString(sqlExecRet.resultSet)
        // 调用 transn 的 chart gen ，获取用于生成 chart 的相关信息
        val chartGenReqJSON = ChartGenReqJSON(inputText, exampleRows)
        val chartGenRespJSON = try {
            transnClient.chartGen(chartGenReqJSON)
        } catch (e: RestClientException) {
            val errorMsg = "Transn API（chart-gen）状态码错误：${e.message}"
            throw LogicalException.create(Err.AI_BACKEND_CALL_ERROR, errorMsg)
        }
        if (chartGenRespJSON.code != 0) {
            val errorMsg = "Transn API（chart-gen）调用结果出现错误：${chartGenRespJSON.msg}"
            throw LogicalException.create(Err.AI_BACKEND_CALL_ERROR, errorMsg)
        }
        val chartGenRespEntity = chartGenRespJSON.data
        // 生成的图表类型，并转换相应的 result set
        val chartType = adaptChartType(chartGenRespEntity.chart)
        val (adaptedResultSet, errorMsg) = adaptResultSet(chartType, resultSet, chartGenRespEntity)
        if (adaptedResultSet == null) {
            throw LogicalException.create(Err.RESULT_SET_NOT_MATCH, errorMsg)
        }
        // 记录日志，并返回分析结果
        logger.info(mapOf("Q" to inputText, "SQL" to sql, "chart" to chartType.nm).toString())
        return ChatAnalysisResult.create(chartType, sql, SQLQueryExecResult.wrapResultSet(adaptedResultSet), nl2sqlRespEntity.guess)
    }

    /**
     * 从 result set 中取出一部分 example rows，并将其转换为 transn 所需要的 string 填入 prompt 中
     */
    private fun buildExampleRowsString(resultSet: RelationalResultSet): String {
        // 判空
        if (resultSet.isEmpty()) {
            return ""
        }
        val tableJoiner = StringJoiner("\n")
        // 拼接 headers
        val tableHeaders = resultSet[0].keys.toList()
        val tableHeadersStringJoiner = StringJoiner("\t")
        tableHeaders.forEach(tableHeadersStringJoiner::add)
        tableJoiner.add(tableHeadersStringJoiner.toString())
        // 拼接 3 个 example rows
        val exampleRows = resultSet.subList(0, EXAMPLE_ROWS_NUMBER)  // 若不足 3 个，则 subList 只返回已存在的元素
        for (row in exampleRows) {
            val rowJoiner = StringJoiner("\t")
            for (header in tableHeaders) {
                val cellValue = row[header]
                rowJoiner.add(cellValue.toString())
            }
            tableJoiner.add(rowJoiner.toString())
        }
        return tableJoiner.toString()
    }

    /**
     * Chart Type 转换
     *
     * 将接口返回的 chart type 转换为枚举的类型
     */
    private fun adaptChartType(chartTypeOfAI: String): ChartTypesEnum {
        return when (chartTypeOfAI.lowercase()) {
            "bar" -> ChartTypesEnum.BAR
            "line" -> ChartTypesEnum.LINE
            "none" -> ChartTypesEnum.TEXT
            else -> ChartTypesEnum.NOT_SUPPORTED
        }
    }

    /**
     * 根据 chart type 和 chart-gen resp 的相关配置要求，将 result set 转为合适的 result set，使其能够下一步被 processor 处理
     */
    private fun adaptResultSet(chartType: ChartTypesEnum, resultSet: RelationalResultSet, chartGenRespEntity: ChartGenRespEntityData): Pair<RelationalResultSet?, String> {
        return when (chartType) {
            ChartTypesEnum.LINE -> adaptResultSetOfLineChart(resultSet, chartGenRespEntity.x, chartGenRespEntity.y)
            else -> Pair(null, "暂不支持的 chart type")
        }
    }

    /**
     * line chart 所需要的元素格式：{name: xxx, value: xxx}
     */
    private fun adaptResultSetOfLineChart(resultSet: RelationalResultSet, x: String?, y: String?): Pair<RelationalResultSet?, String> {
        if (x == null || y == null) {
            return Pair(null, "缺少 x 或 y 轴信息")
        }
        // 检查 row 是否拥有 x、y 字段
        val row = resultSet[0]
        if (!row.containsKey(x)) {
            return Pair(null, "result set 缺少 $x 字段")
        }
        if (!row.containsKey(y)) {
            return Pair(null, "result set 缺少 $y 字段")
        }
        val adaptedResultSet = resultSet.stream().map { mapOf("name" to it[x], "value" to it[y]) }.toList()
        return Pair(adaptedResultSet, "")
    }

}