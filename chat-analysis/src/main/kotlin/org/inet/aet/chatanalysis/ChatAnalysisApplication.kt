package org.inet.aet.chatanalysis

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties
@ConfigurationPropertiesScan
class ChatAnalysisApplication

fun main(args: Array<String>) {
    runApplication<ChatAnalysisApplication>(*args)
}
