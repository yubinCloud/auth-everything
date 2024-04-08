package org.inet.aet.uappmaker

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.mongodb.config.EnableMongoAuditing

@SpringBootApplication
@EnableFeignClients
@EnableMongoAuditing
class UappMakerApplication

fun main(args: Array<String>) {
	runApplication<UappMakerApplication>(*args)
}
