package com.kfd.authenticationservice.dtos.users

data class UserAuthenticationResponse(
    val id: Long,
    val hashedPassword: String
)
