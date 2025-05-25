package com.kfd.noteservice.controllers

import com.kfd.noteservice.database.entities.Note
import com.kfd.noteservice.dto.message.MessageRequestDto
import com.kfd.noteservice.dto.note.NoteRequestDto
import com.kfd.noteservice.dto.note.NoteResponseDto
import com.kfd.noteservice.services.MessageService
import com.kfd.noteservice.services.NoteService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/notes")
class NoteController(
    private val noteService: NoteService,
    private val messageService: MessageService
) {

    private fun Note.toResponseDto(): NoteResponseDto = NoteResponseDto(
        title = this.title,
        body = this.body
    )
    private fun List<Note>.toResponseDtoList(): List<NoteResponseDto> = this.map { it.toResponseDto() }


    @GetMapping
    fun getAllUserNotes(
        @RequestHeader("X-User-Id") userId: String,
    ) : ResponseEntity<List<NoteResponseDto>> {
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
        @RequestBody body: NoteRequestDto
    ) {

    }

    @PutMapping("/{id}")
    fun updateNote(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
        @RequestBody body: NoteRequestDto
    ) {

    }

    @GetMapping("/{id}")
    fun getNote(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
    ) {

    }

    @DeleteMapping("/{id}")
    fun deleteNote(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
    ) : ResponseEntity<Void> {
        noteService.deleteNote(id, userId.toLong())
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/message/create")
    fun sendMessage(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
        @RequestBody body: MessageRequestDto
    ) {

    }
}