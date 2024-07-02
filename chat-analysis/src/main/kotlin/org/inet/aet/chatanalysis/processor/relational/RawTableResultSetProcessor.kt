package org.inet.aet.chatanalysis.processor.relational

import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.entity.RawTableEchartsFormat
import org.inet.aet.chatanalysis.processor.RelationalDBResultSetProcessor
import org.inet.aet.chatanalysis.typealiases.RelationalResultSet
import org.springframework.stereotype.Component

/**
 * 关系型数据库 result set -> RawTable 所需格式
 */
@Component
class RawTableResultSetProcessor: RelationalDBResultSetProcessor {
    override fun forChartType(): ChartTypesEnum {
        return ChartTypesEnum.RAW_TABLE
    }

    override fun process(resultSet: RelationalResultSet): RawTableEchartsFormat {
        return resultSet
    }
}