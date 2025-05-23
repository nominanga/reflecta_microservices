package com.kfd.authenticationservice.dto.users

data class UserAuthenticationResponse(
    val id: String,
    val hashedPassword: String
)
