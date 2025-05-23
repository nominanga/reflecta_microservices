package com.kfd.authenticationservice.dtos.auth.requests

data class LogoutRequest(
    val refreshToken: String
)
