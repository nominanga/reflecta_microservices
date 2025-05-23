package com.kfd.authenticationservice.dtos.auth.requests

data class RefreshRequest(
    val refreshToken: String,
    val sessionId: String
)
