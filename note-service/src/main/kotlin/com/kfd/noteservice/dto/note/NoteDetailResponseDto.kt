package com.kfd.noteservice.dto.note

import com.kfd.noteservice.dto.message.MessageResponseDto
import java.time.LocalDateTime

data class NoteDetailResponseDto(
    val title: String?,
    val body: String,
    val favorite: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val messages: List<MessageResponseDto>
)
