package org.inet.aet.chatanalysis.service.itfce

import org.inet.aet.chatanalysis.constant.AIServBackendFuncEnum
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.entity.Chat2SQLResult
import org.inet.aet.chatanalysis.entity.ChatAnalysisResult
import org.inet.aet.chatanalysis.exception.AIServBackendNotSupportException


interface AIBackendService {

    /**
     * 列出该 AI backend 所支持的功能
     *
     * 每个元素是一个 enum，代表了该 AI 后端所支持的一个功能
     */
    fun supportFunctions(): Set<AIServBackendFuncEnum>

    /**
     * 根据用户的请求，转为分析的 chart
     */
    fun nl2chart(chatReq: Chat2ChartRequest): ChatAnalysisResult {
        throw AIServBackendNotSupportException("当前 AI 后端不支持 NL2Chart 功能")
    }

    fun nl2sql(chatReq: Chat2ChartRequest): Chat2SQLResult {
        throw AIServBackendNotSupportException("当前 AI 后端不支持 NL2SQL 功能")
    }

}