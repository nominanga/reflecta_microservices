package com.kfd.userservice.services.clients

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "authentication-service")
interface AuthenticationServiceClient {
    @PostMapping("/api/auth/logout/all")
    fun logoutAll(
        @RequestHeader("X-User-Id") userId: String
    ) : Unit
}