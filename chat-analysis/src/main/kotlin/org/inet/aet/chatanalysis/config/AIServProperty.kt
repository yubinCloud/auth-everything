package org.inet.aet.chatanalysis.config

import org.springframework.boot.context.properties.ConfigurationProperties


data class TransnBackendProp (
    var baseUrl: String
)

data class OpenAIProp (
    var modelName: String?,
    var apiKey: String?,
    var baseUrl: String?
)

@ConfigurationProperties(prefix = "ai-serv")
class AIServProperty (
    var backend: String,

    // transn 相关配置
    var transnBackend: TransnBackendProp,

    // openai 相关配置
    var openai: OpenAIProp,
)