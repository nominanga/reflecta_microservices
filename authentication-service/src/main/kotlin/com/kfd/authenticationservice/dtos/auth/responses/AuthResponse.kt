package com.kfd.authenticationservice.dtos.auth.responses

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String
)
