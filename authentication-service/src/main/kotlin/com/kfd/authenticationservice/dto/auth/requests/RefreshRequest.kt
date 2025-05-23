package com.kfd.authenticationservice.dto.auth.requests

data class RefreshRequest(
    val refreshToken: String,
    val sessionId: String
)
