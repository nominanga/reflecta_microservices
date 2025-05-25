package com.kfd.noteservice.services

import com.kfd.noteservice.database.entities.Message
import com.kfd.noteservice.database.entities.Note
import com.kfd.noteservice.database.entities.NoteThread
import com.kfd.noteservice.database.repositories.NoteRepository
import com.kfd.noteservice.dto.note.NoteRequestDto
import com.kfd.noteservice.enums.MessageSender
import jakarta.persistence.EntityNotFoundException
import jakarta.ws.rs.ForbiddenException
import org.springframework.stereotype.Service

@Service
class NoteService (
    private val repository: NoteRepository,
    private val messageService: MessageService
){

    private fun createAiMessage(userId: Long, note: Note, previousMessages: List<Message>): Message {
        val aiResponse = "some response"
        return messageService.createMessage(note.noteThread!!, aiResponse, MessageSender.AI)
    }

    private fun generateTitle(body: String) : String {
        return "some response"
    }

    fun createUserMessage(noteId: Long, userId: Long, text: String?) : Message {
        val note = getNote(noteId, userId)
        if (text.isNullOrBlank()) {
            throw IllegalArgumentException("Message can not be empty")
        }
        messageService.createMessage(note.noteThread!!, text, MessageSender.USER)

        val messages = messageService.getMessages(note.noteThread!!)

        return createAiMessage(userId, note, messages)
    }

    fun createNote(userId: Long, noteRequestDto: NoteRequestDto) : Note {
        if (noteRequestDto.body.isNullOrBlank()) {
            throw IllegalArgumentException("Note can not be empty")
        }

        val note = Note(
            userId = userId,
            title = if (!noteRequestDto.title.isNullOrBlank()) noteRequestDto.title
            else generateTitle(noteRequestDto.body),
            body = noteRequestDto.body
        )

        val noteThread = NoteThread(
            note = note
        )
        note.noteThread = noteThread

        // TODO send to deepseek new created note
        val messages = messageService.getMessages(note.noteThread!!)
        createAiMessage(userId, note, messages)

        return repository.save(note)
    }

    fun getNote(noteId: Long, userId: Long) : Note {

        val note = repository.findById(noteId)
            .orElseThrow { throw EntityNotFoundException("Note not found") }
        if (userId != note.userId) {
            throw ForbiddenException("This note does not belong to this user")
        }

        return note
    }

    fun getAllUserNotes(userId: Long): List<Note> {
        return repository.findAllByUserIdOrderByCreatedAtDesc(userId)
    }

    fun getUserFavoriteNotes(userId: Long): List<Note> {
        return repository.findAllByUserIdAndFavoriteTrueOrderByCreatedAtDesc(userId)
    }

    fun updateNote(noteId: Long, userId: Long, noteRequestDto: NoteRequestDto) : Note {
        if (noteRequestDto.body.isNullOrBlank()) {
            throw IllegalArgumentException("Note can not be empty")
        }

        val note = getNote(noteId, userId)
        note.title = if (!noteRequestDto.title.isNullOrBlank()) noteRequestDto.title
        else generateTitle(noteRequestDto.body)
        note.body = noteRequestDto.title ?: generateTitle(noteRequestDto.body)

        val noteThread = NoteThread(
            note = note
        )
        note.noteThread = noteThread

        // TODO send to deepseek new created note
        val messages = messageService.getMessages(note.noteThread!!)
        createAiMessage(userId, note, messages)

        return repository.save(note)
    }

    fun deleteNote(noteId: Long, userId: Long) {
        val note = getNote(noteId, userId)
        repository.delete(note)
    }

    fun noteSetFavorite(noteId: Long, userId: Long) {
        val note = getNote(noteId, userId)
        note.favorite = !note.favorite
        repository.save(note)
    }


}