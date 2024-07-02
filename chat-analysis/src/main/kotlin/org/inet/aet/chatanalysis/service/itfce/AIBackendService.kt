package org.inet.aet.chatanalysis.service.itfce

import org.inet.aet.chatanalysis.constant.AIServBackendFuncEnum
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.entity.Chat2SQLResult
import org.inet.aet.chatanalysis.entity.ChatAnalysisResult


interface AIBackendService {

    /**
     * 该 AI backend 所支持的功能
     */
    fun supportFunctions(): Set<AIServBackendFuncEnum>

    /**
     * 根据用户的请求，转为分析的 chart
     */
    fun nl2chart(chatReq: Chat2ChartRequest): ChatAnalysisResult

    fun nl2sql(chatReq: Chat2ChartRequest): Chat2SQLResult

}