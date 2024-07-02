package org.inet.aet.chatanalysis.processor.relational

import org.inet.aet.chatanalysis.constant.BzExceptionReason
import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.entity.PieEchartsFormat
import org.inet.aet.chatanalysis.entity.PieEchartsFormatItem
import org.inet.aet.chatanalysis.exception.ResultSetFormatException
import org.inet.aet.chatanalysis.processor.RelationalDBResultSetProcessor
import org.inet.aet.chatanalysis.typealiases.RelationalResultSet
import org.springframework.stereotype.Component
import java.math.BigDecimal
import kotlin.streams.toList

/**
 * 关系型 result set -> Pie 所需格式
 */
@Component
class PieResultSetProcessor: RelationalDBResultSetProcessor {
    override fun forChartType(): ChartTypesEnum {
        return ChartTypesEnum.PIE
    }

    override fun process(resultSet: RelationalResultSet): PieEchartsFormat {
        // 先检查是否为空
        if (resultSet.isEmpty()) {
            throw ResultSetFormatException(BzExceptionReason.EMPTY_RESULT_SET)
        }
        var rs = resultSet.stream().map { it.toMutableMap() }.toList()
        // 检查类型
        val row = rs[0]
        // 检查是否存在 field 缺失问题
        if (!row.containsKey("name")) {
            throw ResultSetFormatException(BzExceptionReason.RESULT_SET_ABSENT_FIELD("name"))
        }
        if (!row.containsKey("value")) {
            throw ResultSetFormatException(BzExceptionReason.RESULT_SET_ABSENT_FIELD("value"))
        }
        // 检查是否存在类型错误
        if (row["name"] !is String) {
            throw ResultSetFormatException(BzExceptionReason.RESULT_SET_KLASS_ERROR("name", "string"))
        }
        if (row["value"] !is Number) {
            if (row["value"].toString().toBigDecimalOrNull() == null) {
                throw ResultSetFormatException(BzExceptionReason.RESULT_SET_KLASS_ERROR("value", "number"))
            }
            rs = rs.stream().map {
                val v = it["value"].toString().toBigDecimalOrNull()
                it["value"] = if (v is BigDecimal) v else BigDecimal(0)
                it
            }.toList()
        }
        // 转换为 Pie format
        return rs.stream().map { PieEchartsFormatItem(it["name"] as String, it["value"] as BigDecimal) }.toList()
    }
}