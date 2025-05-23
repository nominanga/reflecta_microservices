package com.kfd.authenticationservice.dtos.auth.requests

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegistrationRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:Email(message = "Invalid email format")
    @field:NotBlank(message = "Email is required")
    val email: String,

    @field:NotBlank(message = "Password is not set")
    @field:Size(min = 8, max = 20, message = "Password must be from 8 to 20 characters long")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+\$",
        message = "Password must contain at least one lowercase, one uppercase and one digit"
    )
    var password: String
)
