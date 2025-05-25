package com.kfd.noteservice.dto.note

import com.kfd.noteservice.dto.message.MessageResponseDto

data class NoteDetailResponseDto(
    val title: String,
    val text: String,
    val messages: List<MessageResponseDto>
)
