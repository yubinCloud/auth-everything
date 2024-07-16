package org.inet.aet.chatanalysis.constant

import org.inet.aet.chatanalysis.service.itfce.AIBackendService


/**
 * 枚举各种 AI Service 的 backend
 */
enum class AIServBackendEnum (val backendId: String, val zhName: String) {

    LOCAL_TEST("local-test", "本地 mock"),
    TRANSN("transn", "Transn"),
    OPENAI("openai", "OpenAI"),

}