package org.inet.aet.chatanalysis.processor


import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.typealiases.RelationalResultSet

/**
 * 用于处理 SQL 执行结果的 result set 的 processor
 * 将 result set 转为 echarts 显示所需要的数据格式
 *
 * 使用时，processor 类需要实现这个接口，并标注 `@Component` 注解：
 *      - forChartType() 方法用于指示这个 processor 是用于转换为哪种 echarts 的
 *      - process() 方法用于完成转换逻辑
 */
interface RelationalDBResultSetProcessor {

    /**
     * 返回所用于转换的 chart type
     */
    fun forChartType(): ChartTypesEnum

    /**
     * 将 ResultSet 转为前端需要的特定 Format
     */
    fun process(resultSet: RelationalResultSet): Any
}
