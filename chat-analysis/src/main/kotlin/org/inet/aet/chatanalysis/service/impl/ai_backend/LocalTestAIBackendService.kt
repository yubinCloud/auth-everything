package org.inet.aet.chatanalysis.service.impl.ai_backend

import org.inet.aet.chatanalysis.constant.AIServBackendFuncEnum
import org.inet.aet.chatanalysis.constant.ChartTypesEnum
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.entity.ChatAnalysisResult
import org.inet.aet.chatanalysis.service.itfce.AIBackendService
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Service

@Service
@Lazy
class LocalTestAIBackendService: AIBackendService {

    override fun supportFunctions(): Set<AIServBackendFuncEnum> {
       return setOf(AIServBackendFuncEnum.NL2CHART)
    }

    override fun nl2chart(chatReq: Chat2ChartRequest): ChatAnalysisResult {
        return ChatAnalysisResult.create(
            ChartTypesEnum.BAR,
            "SELECT * FROM user",
            null,
            listOf("今年的销售收入较去年相比增长了多少？", "有哪些产品或服务贡献了今年销售收入的主要增长？")
        )
    }

}