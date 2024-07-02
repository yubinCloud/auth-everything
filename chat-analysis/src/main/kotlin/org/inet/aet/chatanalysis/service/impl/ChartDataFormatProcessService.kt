package org.inet.aet.chatanalysis.service.impl

import org.inet.aet.chatanalysis.constant.BzExceptionReason
import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.exception.BaseBzException
import org.inet.aet.chatanalysis.processor.RelationalDBResultSetProcessor
import org.inet.aet.chatanalysis.processor.config.RelationalResultSetProcessorMap
import org.inet.aet.chatanalysis.typealiases.RelationalResultSet
import org.springframework.stereotype.Service

/**
 * 用于处理返回给前端的 echarts 的数据格式的 process 服务
 */
@Service
class ChartDataFormatProcessService (private val relationalResultSetProcessorMap: RelationalResultSetProcessorMap) {

    /**
     * 根据 chart type 选出合适的 processor
     */
    private fun choiceProcessor(chartType: ChartTypesEnum): RelationalDBResultSetProcessor {
        val processor = relationalResultSetProcessorMap[chartType]
            ?: throw BaseBzException(BzExceptionReason.CHART_TYPE_NOT_SUPPORT(chartType.nm))
        return processor
    }

    /**
     * 根据 chart type，将 result set 转为合适的 processor
     */
    fun processRelationalResultSet(chartType: ChartTypesEnum, resultSet: RelationalResultSet): Any {
        val processor = choiceProcessor(chartType)
        return processor.process(resultSet)
    }

}