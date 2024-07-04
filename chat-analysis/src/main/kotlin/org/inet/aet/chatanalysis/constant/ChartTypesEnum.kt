package org.inet.aet.chatanalysis.constant

/**
 * 生成的图片的类型
 */
enum class ChartTypesEnum (var nm: String) {
    TEXT("text"),   // 不包含图
    LINE("line"),  // 折线图
    BAR("bar"),    // 柱状图
    PIE("pie"),    // 饼状图
    RAW_TABLE("raw"),  // 原生 table
    NOT_SUPPORTED("not supported"),  // 暂未支持的 chart type
}