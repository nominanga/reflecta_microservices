package com.kfd.noteservice.dto.ai

data class AiRequestDto (
    val username: String,
    val messages: List<AiMessageDto>
)