package com.kfd.noteservice.dto.note

import com.kfd.noteservice.dto.message.MessageResponseDto
import java.time.LocalDateTime

data class NoteDetailedResponseDto(
    val id: Long,
    val title: String?,
    val body: String,
    val favorite: Boolean,
    val createdAt: LocalDateTime,
    val messages: List<MessageResponseDto>
)
