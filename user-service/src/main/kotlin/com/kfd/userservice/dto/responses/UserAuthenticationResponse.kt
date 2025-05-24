package com.kfd.userservice.dto.responses

data class UserAuthenticationResponse(
    val id: String,
    val hashedPassword: String
)
