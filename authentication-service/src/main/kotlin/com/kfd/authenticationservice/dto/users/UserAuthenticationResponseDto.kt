package com.kfd.authenticationservice.dto.users

data class UserAuthenticationResponseDto(
    val id: String,
    val hashedPassword: String
)
