package org.inet.aet.chatanalysis.processor.relational

import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.entity.LineEchartsFormat
import org.inet.aet.chatanalysis.processor.RelationalDBResultSetProcessor
import org.inet.aet.chatanalysis.typealiases.RelationalResultSet
import org.springframework.stereotype.Component

/**
 * 关系型数据库的查询结果 -> Line 所需格式
 *
 * result set 要求格式：[{name: xxx, value: xxx}, {...}]
 */
@Component
class LineResultSetProcessor (private val barResultSetProcessor: BarResultSetProcessor): RelationalDBResultSetProcessor {
    override fun forChartType(): ChartTypesEnum {
        return ChartTypesEnum.LINE
    }

    override fun process(resultSet: RelationalResultSet): LineEchartsFormat {
        // 这里直接复用 bar processor 的处理逻辑
        val barEchartsFormat = barResultSetProcessor.process(resultSet)
        return LineEchartsFormat(barEchartsFormat.x, barEchartsFormat.y)
    }
}