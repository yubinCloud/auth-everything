package org.inet.aet.chatanalysis.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.inet.aet.chatanalysis.entity.DataSourceConf

@Schema(description = "对话生成报表的请求")
data class Chat2ChartRequest (

    @NotBlank(message = "输入不允许为空")
    var input: String,

    @NotNull
    var dataSourceConf: DataSourceConf,

    @NotNull(message = "策略不允许为空")
    var genStrategy: Map<String, Any>
)
