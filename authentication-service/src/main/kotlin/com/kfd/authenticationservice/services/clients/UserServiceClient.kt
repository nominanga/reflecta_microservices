package com.kfd.authenticationservice.services.clients

import com.kfd.authenticationservice.dto.auth.requests.RegistrationRequestDto
import com.kfd.authenticationservice.dto.users.UserAuthenticationResponseDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam

@FeignClient(name = "user-service")
interface UserServiceClient {
    @PostMapping("/internal/user")
    fun createUser(
        @RequestBody request: RegistrationRequestDto,
    ): UserAuthenticationResponseDto

    @GetMapping("/internal/user/email")
    fun getUserByEmail(
        @RequestParam email: String,
    ): UserAuthenticationResponseDto

    @GetMapping("/internal/user/email/exists")
    fun existsUserByEmail(
        @RequestParam email: String,
    ): Boolean
}
