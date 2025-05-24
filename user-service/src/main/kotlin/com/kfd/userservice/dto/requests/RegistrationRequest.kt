package com.kfd.userservice.dto.requests

data class RegistrationRequest(
    val username: String,
    val email: String,
    var password: String
)

