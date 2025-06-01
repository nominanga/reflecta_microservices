package com.kfd.userservice.dto.requests

data class RegistrationRequestDto(
    val username: String,
    val email: String,
    var password: String,
)
