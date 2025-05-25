package com.kfd.noteservice.dto.note

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class NoteRequestDto(
    @field:Size(max = 30, message = "Title must be at most 30 characters")
    val title: String?,

    @field:NotBlank(message = "Body must not be blank")
    val body: String
)
