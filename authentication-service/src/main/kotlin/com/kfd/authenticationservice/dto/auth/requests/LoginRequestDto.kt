package com.kfd.authenticationservice.dto.auth.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Invalid email")
    val email: String,
    @field:NotBlank(message = "Password is required")
    val password: String
)
