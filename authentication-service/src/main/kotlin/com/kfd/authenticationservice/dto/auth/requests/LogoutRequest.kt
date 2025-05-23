package com.kfd.authenticationservice.dto.auth.requests

data class LogoutRequest(
    val refreshToken: String
)
