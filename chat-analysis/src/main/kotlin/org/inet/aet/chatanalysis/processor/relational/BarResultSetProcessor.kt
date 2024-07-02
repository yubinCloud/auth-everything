package org.inet.aet.chatanalysis.processor.relational

import org.inet.aet.chatanalysis.constant.BzExceptionReason
import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.entity.BarEchartsFormat
import org.inet.aet.chatanalysis.exception.ResultSetFormatException
import org.inet.aet.chatanalysis.processor.RelationalDBResultSetProcessor
import org.inet.aet.chatanalysis.typealiases.RelationalResultSet
import org.springframework.stereotype.Component
import java.math.BigDecimal
import kotlin.streams.toList

/**
 * 关系型数据库的查询结果 -> Bar 所需格式
 */
@Component
class BarResultSetProcessor: RelationalDBResultSetProcessor {

    override fun forChartType(): ChartTypesEnum {
        return ChartTypesEnum.BAR
    }

    override fun process(resultSet: RelationalResultSet): BarEchartsFormat {
        // 先检查是否为空
        if (resultSet.isEmpty()) {
            throw ResultSetFormatException(BzExceptionReason.EMPTY_RESULT_SET)
        }
        var rs = resultSet.stream().map { it.toMutableMap() }.toList()
        // 检查类型
        val exampleRow = rs[0]
        // 检查是否存在 field 缺失问题
        if (!exampleRow.containsKey("name")) {
            throw ResultSetFormatException(BzExceptionReason.RESULT_SET_ABSENT_FIELD("name"))
        }
        if (!exampleRow.containsKey("value")) {
            throw ResultSetFormatException(BzExceptionReason.RESULT_SET_ABSENT_FIELD("value"))
        }
        // 检查是否存在类型错误
        if (exampleRow["name"] !is String) {
            throw ResultSetFormatException(BzExceptionReason.RESULT_SET_KLASS_ERROR("name", "string"))
        }
        if (exampleRow["value"] !is Number) {
            if (exampleRow["value"].toString().toBigDecimalOrNull() == null) {
                throw ResultSetFormatException(BzExceptionReason.RESULT_SET_KLASS_ERROR("value", "number"))
            }
            rs = rs.stream().map {
                    val v = it["value"].toString().toBigDecimalOrNull()
                    it["value"] = if (v is BigDecimal) v else BigDecimal(0)
                    it
                }.toList()
        }
        // 转换为 Bar Format
        val xAxis = rs.stream().map { it["name"] as String }.toList()
        val yAxis = rs.stream().map { it["value"] as BigDecimal }.toList()
        return BarEchartsFormat(xAxis, yAxis)
    }
}