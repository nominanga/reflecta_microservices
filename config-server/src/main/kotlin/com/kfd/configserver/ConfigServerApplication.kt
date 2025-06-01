@file:Suppress("SpreadOperator")

package com.kfd.configserver

import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.config.server.EnableConfigServer

@SpringBootApplication
@EnableConfigServer
class ConfigServerApplication {
    @PostConstruct
    fun logEnv() {
        println("ACCESS_TOKEN = ${System.getenv("ACCESS_TOKEN")}")
    }
}

fun main(args: Array<String>) {
    runApplication<ConfigServerApplication>(*args)
}
