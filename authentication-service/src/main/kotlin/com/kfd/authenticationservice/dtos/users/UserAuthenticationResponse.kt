package com.kfd.authenticationservice.dtos.users

data class UserAuthenticationResponse(
    val id: String,
    val hashedPassword: String
)
