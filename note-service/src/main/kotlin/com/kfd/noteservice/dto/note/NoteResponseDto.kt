package com.kfd.noteservice.dto.note

data class NoteResponseDto(
    val title: String?,
    val body: String,
    val favorite: Boolean,
)
