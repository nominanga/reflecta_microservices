package com.kfd.noteservice.dto.message

import com.kfd.noteservice.enums.MessageSender

data class MessageResponseDto(
    val text: String,
    val sender: MessageSender
)