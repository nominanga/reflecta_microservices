package com.kfd.authenticationservice.dto.auth.responses

data class AuthResponseDto(
    val accessToken: String,
    val refreshToken: String,
    val sessionId: String,
)
