package com.kfd.authenticationservice.dto.auth.responses

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val sessionId: String
)
