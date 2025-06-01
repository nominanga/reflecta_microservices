package com.kfd.noteservice.controllers

import com.kfd.noteservice.database.entities.Note
import com.kfd.noteservice.dto.message.MessageRequestDto
import com.kfd.noteservice.dto.message.MessageResponseDto
import com.kfd.noteservice.dto.note.NoteDetailedResponseDto
import com.kfd.noteservice.dto.note.NoteRequestDto
import com.kfd.noteservice.dto.note.NoteResponseDto
import com.kfd.noteservice.services.AiMessageService
import com.kfd.noteservice.services.NoteService
import com.kfd.noteservice.utils.NoteDtoUtils
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val noteService: NoteService,
    private val aiMessageService: AiMessageService,
    private val noteDtoUtils: NoteDtoUtils,
) {
    private fun Note.toResponseDto(): NoteResponseDto =
        NoteResponseDto(
            id = this.id,
            title = this.title,
            body = this.body,
            favorite = this.favorite,
        )

    private fun List<Note>.toResponseDtoList(): List<NoteResponseDto> = this.map { it.toResponseDto() }

    @GetMapping
    fun getAllUserNotes(
        @RequestHeader("X-User-Id") userId: String,
    ): ResponseEntity<List<NoteResponseDto>> {
        val notesList = noteService.getAllUserNotes(userId.toLong())
        return ResponseEntity.ok(notesList.toResponseDtoList())
    }

    @GetMapping("/favorite")
    fun getUserFavoriteNotes(
        @RequestHeader("X-User-Id") userId: String,
    ): ResponseEntity<List<NoteResponseDto>> {
        val notesList = noteService.getUserFavoriteNotes(userId.toLong())
        return ResponseEntity.ok(notesList.toResponseDtoList())
    }

    @PostMapping("/create")
    fun createNote(
        @RequestHeader("X-User-Id") userId: String,
        @RequestBody @Valid body: NoteRequestDto,
    ): ResponseEntity<NoteDetailedResponseDto> {
        val note = noteService.createNote(userId.toLong(), body)
        return ResponseEntity.ok(noteDtoUtils.mapNoteToDetailedDto(note))
    }

    @PutMapping("/{id:\\d+}/set-favorite")
    fun noteSetFavorite(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
    ): ResponseEntity<Void> {
        noteService.noteSetFavorite(id, userId.toLong())
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{id:\\d+}")
    fun updateNote(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
        @RequestBody @Valid body: NoteRequestDto,
    ): ResponseEntity<NoteDetailedResponseDto> {
        val note = noteService.updateNote(id, userId.toLong(), body)
        return ResponseEntity.ok(noteDtoUtils.mapNoteToDetailedDto(note))
    }

    @GetMapping("/{id:\\d+}")
    fun getNote(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
    ): ResponseEntity<NoteDetailedResponseDto> {
        val note = noteService.getNote(id, userId.toLong())
        return ResponseEntity.ok(noteDtoUtils.mapNoteToDetailedDto(note))
    }

    @DeleteMapping("/{id:\\d+}")
    fun deleteNote(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
    ): ResponseEntity<Void> {
        noteService.deleteNote(id, userId.toLong())
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id:\\d+}/send-message")
    fun sendMessage(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
        @RequestBody body: MessageRequestDto,
    ): ResponseEntity<MessageResponseDto> {
        val note = noteService.getNote(id, userId.toLong())
        val message = aiMessageService.createUserMessage(note, body.text)
        return ResponseEntity.ok(noteDtoUtils.mapMessageToResponseDto(message))
    }
}
