package org.inet.aet.chatanalysis.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.inet.aet.chatanalysis.chatfilter.itfce.Chat2ChartFilter
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.dto.response.Chat2ChartResponse
import org.inet.aet.chatanalysis.dto.response.R
import org.inet.aet.chatanalysis.dto.response.R_SUCCESS
import org.inet.aet.chatanalysis.service.impl.ChatService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/chat2chart")
@Validated
@Tag(name = "对话生成报表")
class Chat2ChartController(
    private val chatService: ChatService,
    private val chat2ChartFilters: List<Chat2ChartFilter>
) {

    // 进行对话处理前，进行链式过滤的 filters
    private val forwardChatFilters: List<Chat2ChartFilter> = chat2ChartFilters.stream().sorted(Comparator.comparing(Chat2ChartFilter::getOrder)).toList()

    @GetMapping("/support-func")
    @Operation(summary = "查看当前 AI backend 所支持的功能")
    fun supportFunctions(): R<List<String>> {
        return R_SUCCESS(chatService.supportFunctions())
    }

    @PostMapping("/generate")
    @Operation(summary = "进行对话")
    fun chatGenerate(@RequestBody @Valid body: Chat2ChartRequest): R<Chat2ChartResponse> {
        println(forwardChatFilters)
        for (filter in chat2ChartFilters) {
            val filterRet = filter.doFilterBefore(body)
            if (filterRet != null) {
                return R_SUCCESS(filterRet)
            }
        }
        val chatResp = chatService.chat2chart(body)
        return R_SUCCESS(chatResp)
    }

}