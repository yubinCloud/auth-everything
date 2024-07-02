package org.inet.aet.chatanalysis.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "对话生成的响应")
data class Chat2ChartResponse (

    @Schema(description = "生成的文本")
    var outputText: String,

    @Schema(description = "图表的类型")
    var chartType: String,

    @Schema(description = "SQL")
    var sql: String,

    @Schema(description = "本次对话是否成功")
    val success: Boolean,

    @Schema(description = "失败的 reason")
    val errorReason: String,

    @Schema(description = "chart content，当 success 为 true 时，一定不为 null")
    var chartContent: Any?,

    @Schema(description = "用户接下来可能会问的问题")
    var nextMaybe: List<String>
)