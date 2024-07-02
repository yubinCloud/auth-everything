package org.inet.aet.chatanalysis.config

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component

@Component
class AIServBackendPrintApplicationRunner(val aiServProperty: AIServProperty): ApplicationRunner {

    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }

    /**
     * 打印当前使用的 AI Backend
     */
    override fun run(args: ApplicationArguments?) {
        log.info("AI Service Backend Type: ${aiServProperty.backend}")
    }
}