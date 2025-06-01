package com.kfd.aiservice.dto

data class AiRequestDto(
    val username: String,
    val messages: List<AiMessageDto>,
)
