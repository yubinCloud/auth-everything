package org.inet.aet.chatanalysis.chatfilter.impl

import org.inet.aet.chatanalysis.chatfilter.itfce.Chat2ChartFilter
import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.dto.response.Chat2ChartResponse
import org.inet.aet.chatanalysis.exception.Err
import org.inet.aet.chatanalysis.exception.LogicalException
import org.inet.aet.chatanalysis.service.impl.ChartDataFormatProcessService
import org.inet.aet.chatanalysis.service.impl.DatasourceService
import org.inet.aet.chatanalysis.typealiases.RelationalResultSet
import org.springframework.stereotype.Component

/**
 * TyDB 数据库作为 Demo 的 filter
 */
@Component
class TyDBDemoFilter(private val datasourceService: DatasourceService, private val chartDataFormatProcessService: ChartDataFormatProcessService) : Chat2ChartFilter {


    private fun makeResp(req: Chat2ChartRequest, sql: String, chartType: ChartTypesEnum): Chat2ChartResponse {
        val (success, reason, records) = datasourceService.execQuery(req.dataSourceConf, sql)
        if (!success) {
            throw LogicalException(Err.SQL_EXEC_ERROR, reason)
        }
        var rs: RelationalResultSet = records.stream().map { it.toMutableMap() }.toList()
        val row = records[0]
        if (row.containsKey("SALES_TYPE")) {
            rs = rs.stream().map { mapOf("name" to it["SALES_TYPE"], "value" to it["value"]) }.toList()
        } else if (row.containsKey("CUSTOMER_NAME")) {
            rs = rs.stream().map { mapOf("name" to it["CUSTOMER_NAME"], "value" to it["value"]) }.toList()
        }
        return Chat2ChartResponse(
            outputText = "",
            chartType = chartType.nm,
            sql = sql,
            chartContent = chartDataFormatProcessService.processRelationalResultSet(chartType, rs),
            nextMaybe = emptyList()
        )
    }

    override fun doFilterBefore(req: Chat2ChartRequest): Chat2ChartResponse? {
        val inputText = req.input
        if (inputText == "统计不同销售收入的总额") {
            val sql = "SELECT SALES_TYPE AS `name`, SUM(TOTAL_AMOUNT) AS `value` FROM SALES_INCOME GROUP BY SALES_TYPE ORDER BY `value`;"
            return makeResp(req, sql, ChartTypesEnum.BAR)
        } else if (inputText == "统计不同客户的订单数量占比") {
            val sql = "SELECT COUNT(*) AS `value`, CUSTOMER_NAME AS `name` FROM SALES_INCOME GROUP BY `name` ORDER BY `value`;"
            return makeResp(req, sql, ChartTypesEnum.PIE)
        } else if (inputText == "回款项已经全部支付的有多少") {
            val sql = "SELECT COUNT(*) as `number` FROM ACCOUNTS_RECEIVABLE WHERE `INVOICE_STATUS` = '已支付';"
            return makeResp(req, sql, ChartTypesEnum.RAW_TABLE)
        }
        return null
    }
}