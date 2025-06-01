@file:Suppress("SpreadOperator")

package com.kfd.noteservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
class NoteServiceApplication

fun main(args: Array<String>) {
    runApplication<NoteServiceApplication>(*args)
}
