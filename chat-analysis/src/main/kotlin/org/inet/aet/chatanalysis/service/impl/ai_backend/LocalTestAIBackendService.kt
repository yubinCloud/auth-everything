package org.inet.aet.chatanalysis.service.impl.ai_backend

import org.inet.aet.chatanalysis.constant.AIServBackendFuncEnum
import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.entity.ChartConf
import org.inet.aet.chatanalysis.entity.Chat2SQLResult
import org.inet.aet.chatanalysis.entity.ChatAnalysisResult
import org.inet.aet.chatanalysis.service.itfce.AIBackendService

class LocalTestAIBackendService: AIBackendService {

    override fun supportFunctions(): Set<AIServBackendFuncEnum> {
       return setOf(AIServBackendFuncEnum.NL2CHART)
    }

    override fun nl2chart(chatReq: Chat2ChartRequest): ChatAnalysisResult {
        return ChatAnalysisResult(
            true,
            "",
            ChartConf(ChartTypesEnum.BAR),
            "SELECT * FROM user",
            null,
            listOf("今年的销售收入较去年相比增长了多少？", "有哪些产品或服务贡献了今年销售收入的主要增长？")
        )
    }

    override fun nl2sql(chatReq: Chat2ChartRequest): Chat2SQLResult {
        TODO("Not yet implemented")
    }

}