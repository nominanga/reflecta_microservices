package com.kfd.noteservice.services

import com.kfd.noteservice.database.entities.Message
import com.kfd.noteservice.database.entities.Note
import com.kfd.noteservice.database.entities.NoteThread
import com.kfd.noteservice.database.repositories.NoteRepository
import com.kfd.noteservice.dto.ai.AiMessageDto
import com.kfd.noteservice.dto.ai.AiRequestDto
import com.kfd.noteservice.dto.note.NoteRequestDto
import com.kfd.noteservice.enums.MessageSender
import com.kfd.noteservice.services.clients.AiServiceClient
import com.kfd.noteservice.services.clients.UserServiceClient
import jakarta.persistence.EntityNotFoundException
import jakarta.ws.rs.ForbiddenException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoteService(
    private val repository: NoteRepository,
    private val userServiceClient: UserServiceClient,
    private val aiServiceClient: AiServiceClient,
    private val messageService: MessageService,
) {
    private fun Message.toAiMessageDto(): AiMessageDto =
        AiMessageDto(
            content = this.text,
            role =
                when (this.sender) {
                    MessageSender.AI -> "assistant"
                    MessageSender.USER -> "user"
                },
        )

    private fun createAiMessage(
        userId: Long,
        note: Note,
        previousMessages: List<Message>,
    ): Message {
        val username = userServiceClient.getUserName(userId)
        val noteAiMessage =
            AiMessageDto(
                content = note.body,
                role = "user",
            )
        val aiRequestDto =
            AiRequestDto(
                username = username,
                messages = listOf(noteAiMessage) + previousMessages.map { it.toAiMessageDto() },
            )

        val aiResponse = aiServiceClient.getAiResponse(aiRequestDto)
        return messageService.createMessage(note.noteThread!!, aiResponse, MessageSender.AI)
    }

    private fun generateTitle(body: String): String {
        return aiServiceClient.generateTitle(body)
    }

    @Transactional
    fun createUserMessage(
        noteId: Long,
        userId: Long,
        text: String?,
    ): Message {
        val note = getNote(noteId, userId)
        if (text.isNullOrBlank()) {
            throw IllegalArgumentException("Message can not be empty")
        }
        messageService.createMessage(note.noteThread!!, text, MessageSender.USER)

        val messages = messageService.getMessages(note.noteThread!!)

        return createAiMessage(userId, note, messages)
    }

    @Transactional
    fun createNote(
        userId: Long,
        noteRequestDto: NoteRequestDto,
    ): Note {
        val note =
            Note(
                userId = userId,
                title =
                    if (!noteRequestDto.title.isNullOrBlank()) {
                        noteRequestDto.title
                    } else {
                        generateTitle(noteRequestDto.body)
                    },
                body = noteRequestDto.body,
            )

        val noteThread = NoteThread(note)
        note.noteThread = noteThread

        val savedNote = repository.save(note)
        val messages = messageService.getMessages(savedNote.noteThread!!)
        createAiMessage(userId, savedNote, messages)

        return savedNote
    }

    fun getNote(
        noteId: Long,
        userId: Long,
    ): Note {
        val note =
            repository.findById(noteId)
                .orElseThrow { throw EntityNotFoundException("Note not found") }
        if (userId != note.userId) {
            throw ForbiddenException("This note does not belong to this user")
        }

        return note
    }

    fun getAllUserNotes(userId: Long): List<Note> {
        return repository.findAllByUserIdOrderByUpdatedAtDesc(userId)
    }

    fun getUserFavoriteNotes(userId: Long): List<Note> {
        return repository.findAllByUserIdAndFavoriteTrueOrderByUpdatedAtDesc(userId)
    }

    @Transactional
    fun updateNote(
        noteId: Long,
        userId: Long,
        noteRequestDto: NoteRequestDto,
    ): Note {
        val note = getNote(noteId, userId)

        note.title =
            if (!noteRequestDto.title.isNullOrBlank()) {
                noteRequestDto.title
            } else {
                generateTitle(noteRequestDto.body)
            }
        note.body = noteRequestDto.body

        messageService.deleteMessages(note.noteThread!!)

        val savedNote = repository.save(note)
        val messages = messageService.getMessages(savedNote.noteThread!!)
        createAiMessage(userId, savedNote, messages)

        return savedNote
    }

    @Transactional
    fun deleteNote(
        noteId: Long,
        userId: Long,
    ) {
        val note = getNote(noteId, userId)
        repository.delete(note)
    }

    @Transactional
    fun noteSetFavorite(
        noteId: Long,
        userId: Long,
    ) {
        val note = getNote(noteId, userId)
        note.favorite = !note.favorite
        repository.save(note)
    }
}
