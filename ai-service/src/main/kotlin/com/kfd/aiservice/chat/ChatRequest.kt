package com.kfd.aiservice.chat

import com.kfd.aiservice.dto.AiMessageDto

data class ChatRequest(
    val model: String,
    val messages: List<AiMessageDto>,
    val stream: Boolean = false
)
