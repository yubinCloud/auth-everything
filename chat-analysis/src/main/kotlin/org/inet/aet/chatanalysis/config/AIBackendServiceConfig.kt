package org.inet.aet.chatanalysis.config

import org.inet.aet.chatanalysis.constant.AIServBackendEnum
import org.inet.aet.chatanalysis.service.impl.DatasourceService
import org.inet.aet.chatanalysis.service.impl.ai_backend.LocalTestAIBackendService
import org.inet.aet.chatanalysis.service.impl.ai_backend.TransnAIBackendService
import org.inet.aet.chatanalysis.service.impl.ai_backend.OpenAIBasicBackendService
import org.inet.aet.chatanalysis.service.itfce.AIBackendService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


/**
 * 根据不同的 AI backend 来选择相应的 `AIBackendService` 接口的实现类
 */
@Configuration
class AIBackendServiceConfig (
    val aiServProperty: AIServProperty,
    val datasourceService: DatasourceService
) {

    companion object {
        private const val AI_SERV_BACKEND_PROP = "ai-serv.backend"
    }


    @Bean
    @ConditionalOnProperty(name = [AI_SERV_BACKEND_PROP], havingValue = AIServBackendEnum.LOCAL_TEST, matchIfMissing = true)
    fun localTestBackend(): AIBackendService {
        return LocalTestAIBackendService()
    }

    @Bean
    @ConditionalOnProperty(name = [AI_SERV_BACKEND_PROP], havingValue = AIServBackendEnum.TRANSN)
    fun transnBackend(): AIBackendService {
        return TransnAIBackendService(aiServProperty, datasourceService)
    }

    @Bean
    @ConditionalOnProperty(name = [AI_SERV_BACKEND_PROP], havingValue = AIServBackendEnum.OPENAI)
    fun zeroShotOpenaiBackend(): AIBackendService {
        return OpenAIBasicBackendService(aiServProperty, datasourceService)
    }
}