package com.kfd.noteservice.services

import com.kfd.noteservice.database.entities.Note
import com.kfd.noteservice.database.entities.NoteThread
import com.kfd.noteservice.database.repositories.NoteRepository
import com.kfd.noteservice.dto.note.NoteRequestDto
import jakarta.persistence.EntityNotFoundException
import jakarta.ws.rs.ForbiddenException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class NoteService(
    private val repository: NoteRepository,
    private val aiMessageService: AiMessageService,
) {
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
                        aiMessageService.generateTitle(noteRequestDto.body)
                    },
                body = noteRequestDto.body,
            )

        val noteThread = NoteThread(note)
        note.noteThread = noteThread

        val savedNote = repository.save(note)
        val messages = aiMessageService.getMessages(savedNote.noteThread!!)
        aiMessageService.createAiMessage(userId, savedNote, messages)

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
                aiMessageService.generateTitle(noteRequestDto.body)
            }
        note.body = noteRequestDto.body

        aiMessageService.deleteMessages(note.noteThread!!)

        val savedNote = repository.save(note)
        val messages = aiMessageService.getMessages(savedNote.noteThread!!)
        aiMessageService.createAiMessage(userId, savedNote, messages)

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
