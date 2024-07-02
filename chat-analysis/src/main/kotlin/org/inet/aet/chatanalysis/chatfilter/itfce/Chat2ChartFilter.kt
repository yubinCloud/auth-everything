package org.inet.aet.chatanalysis.chatfilter.itfce

import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.dto.response.Chat2ChartResponse


/**
 * Chat2Chart 功能在实际运行前，做的一个拦截器，
 *
 * 用于过滤不合法请求，或者用于拦截一些用于演示的 demo
 */
interface Chat2ChartFilter {

    fun getOrder(): Int {
        return 0
    }

    /**
     * 在请求处理之前做拦截操作
     */
    fun doFilterBefore(req: Chat2ChartRequest): Chat2ChartResponse? {
        return null
    }

    /**
     * 在请求处理之后做拦截操作
     */
    fun doFilterAfter(resp: Chat2ChartResponse): Chat2ChartResponse? {
        return null
    }
}