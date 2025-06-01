package com.kfd.noteservice.utils

import com.kfd.noteservice.database.entities.Message
import com.kfd.noteservice.database.entities.Note
import com.kfd.noteservice.dto.message.MessageResponseDto
import com.kfd.noteservice.dto.note.NoteDetailedResponseDto
import com.kfd.noteservice.services.AiMessageService
import org.springframework.stereotype.Component

@Component
class NoteDtoUtils(
    private val aiMessageService: AiMessageService,
) {
    fun mapMessageToResponseDto(message: Message): MessageResponseDto {
        return MessageResponseDto(
            text = message.text,
            sender = message.sender,
        )
    }

    fun mapNoteToDetailedDto(note: Note): NoteDetailedResponseDto {
        val messages = aiMessageService.getMessages(note.noteThread!!)
        return NoteDetailedResponseDto(
            id = note.id,
            title = note.title,
            body = note.body,
            favorite = note.favorite,
            createdAt = note.createdAt,
            messages = messages.map { mapMessageToResponseDto(it) },
        )
    }
}
