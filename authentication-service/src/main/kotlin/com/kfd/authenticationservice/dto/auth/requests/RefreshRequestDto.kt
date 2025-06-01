package com.kfd.authenticationservice.dto.auth.requests

data class RefreshRequestDto(
    val refreshToken: String,
    val sessionId: String,
)
