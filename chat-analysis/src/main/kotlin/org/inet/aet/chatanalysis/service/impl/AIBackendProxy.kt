package org.inet.aet.chatanalysis.service.impl

import org.inet.aet.chatanalysis.config.AIServProperty
import org.inet.aet.chatanalysis.constant.AIServBackendEnum
import org.inet.aet.chatanalysis.constant.AIServBackendFuncEnum
import org.inet.aet.chatanalysis.dto.request.Chat2ChartRequest
import org.inet.aet.chatanalysis.entity.Chat2SQLResult
import org.inet.aet.chatanalysis.entity.ChatAnalysisResult
import org.inet.aet.chatanalysis.service.impl.ai_backend.LocalTestAIBackendService
import org.inet.aet.chatanalysis.service.impl.ai_backend.OpenAIBasicBackendService
import org.inet.aet.chatanalysis.service.impl.ai_backend.TransnAIBackendService
import org.inet.aet.chatanalysis.service.itfce.AIBackendService
import org.springframework.stereotype.Service
import java.util.Hashtable

/**
 * 执行  AI Backend 的功能
 */
@Service
class AIBackendProxy (
    aiServProperty: AIServProperty,
    // *********************************************************
    // ************* 各 AI Backend Services ********************
    // *********************************************************
    val localTestAIBackendService: LocalTestAIBackendService,
    val openAIBasicBackendService: OpenAIBasicBackendService,
    val transnAIBackendService: TransnAIBackendService,
) {


    private val backendMap = run {
        /**
         * 建立 BackendEnum -> BackendService 的 map
         */
        val map = Hashtable<String, AIBackendService>()
        map[AIServBackendEnum.LOCAL_TEST.backendId] = localTestAIBackendService
        map[AIServBackendEnum.OPENAI.backendId] = openAIBasicBackendService
        map[AIServBackendEnum.TRANSN.backendId] = transnAIBackendService
        map
    }

    private var defaultBackendService = run {
        backendMap[aiServProperty.backend] ?: localTestAIBackendService
    }

    private fun choiceAIBackend(backend: String? = null): AIBackendService {
        if (backend == null) {
            return defaultBackendService
        }
        return backendMap[backend] ?: defaultBackendService
    }

    fun changeDefaultAIBackend(backend: String?): Boolean {
        TODO()
    }

    fun supportFunctions(backend: String? = null): Set<AIServBackendFuncEnum> {
        val backendService = choiceAIBackend(backend)
        return backendService.supportFunctions()
    }

    fun nl2chart(chatReq: Chat2ChartRequest, backend: String? = null): ChatAnalysisResult {
        val backendService = choiceAIBackend(backend)
        return backendService.nl2chart(chatReq)
    }

    fun nl2sql(chatReq: Chat2ChartRequest, backend: String? = null): Chat2SQLResult {
        val backendService = choiceAIBackend(backend)
        return backendService.nl2sql(chatReq)
    }
}