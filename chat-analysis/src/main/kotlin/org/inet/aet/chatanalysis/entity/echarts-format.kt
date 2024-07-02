package org.inet.aet.chatanalysis.entity

import java.math.BigDecimal


/**
 * bar 类型的 chart 所需要的数据
 */
data class BarEchartsFormat (
    val xAxis: List<String>,
    val yAxis: List<Number>
)


/**
 * Pie 类型的 chart 所需要的数据
 */
data class PieEchartsFormatItem (
    val name: String,
    val value: Number
)
typealias PieEchartsFormat = List<PieEchartsFormatItem>


/**
 * raw table 类型的 chart 所需要的数据
 */
typealias RawTableEchartsFormat = List<Map<String, Any?>>
