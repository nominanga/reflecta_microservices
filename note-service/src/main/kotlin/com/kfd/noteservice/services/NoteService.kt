package com.kfd.noteservice.services

import com.kfd.noteservice.database.entities.Note
import com.kfd.noteservice.database.entities.NoteThread
import com.kfd.noteservice.database.repositories.NoteRepository
import com.kfd.noteservice.dto.note.NoteRequestDto
import jakarta.persistence.EntityNotFoundException
import jakarta.ws.rs.ForbiddenException
import org.springframework.stereotype.Service

@Service
class NoteService (
    private val repository: NoteRepository
){

    private fun processNote(note: Note) : Note {
        // TODO generate title for note with deepseek

        val noteThread = NoteThread(
            note = note
        )

        note.noteThread = noteThread

        // TODO send to deepseek new created note

        return note
    }

    fun createNote(userId: Long, noteRequestDto: NoteRequestDto) : Note {
        if (noteRequestDto.body.isNullOrBlank()) {
            throw IllegalArgumentException("Note can not be empty")
        }

        val note = Note(
            userId = userId,
            title = noteRequestDto.title,
            body = noteRequestDto.body
        )

        val processedNote = processNote(note)

        return repository.save(processedNote)
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
        note.title = noteRequestDto.title
        note.body = noteRequestDto.body

        val processedNote = processNote(note)

        return repository.save(processedNote)
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