package com.kfd.userservice.dto.requests

data class UserAvatarUpdateDto (
    val userId: String,
    val uri: String,
)