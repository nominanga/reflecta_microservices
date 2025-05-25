package com.kfd.noteservice.services.clients

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*

@FeignClient(name = "user-service")
interface UserServiceClient {

    @GetMapping("internal/user/{id}/username")
    fun getUserName(@PathVariable("id") id: Long): String
}