package com.kfd.userservice.dto.responses

data class UserAuthenticationResponseDto(
    val id: String,
    val hashedPassword: String,
)
