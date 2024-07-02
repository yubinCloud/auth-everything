package org.inet.aet.chatanalysis.constant


/**
 * 枚举各种 AI Service 的 backend
 */
class AIServBackendEnum {

    companion object {
        const val LOCAL_TEST = "local-test"  // 本地 mock 的接口来测试
        const val TRANSN = "transn"   // 航天云网项目中，传神（Transn）的 AI 后端
        const val OPENAI = "openai"   // OpenAI 后端
    }

}