package com.example.afgateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
@EnableCaching
class AfGatewayApplication

fun main(args: Array<String>) {
    runApplication<AfGatewayApplication>(*args)
}
