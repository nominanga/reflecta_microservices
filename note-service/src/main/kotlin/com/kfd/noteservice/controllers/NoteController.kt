package com.kfd.noteservice.controllers

import com.kfd.noteservice.database.entities.Message
import com.kfd.noteservice.database.entities.Note
import com.kfd.noteservice.dto.message.MessageRequestDto
import com.kfd.noteservice.dto.message.MessageResponseDto
import com.kfd.noteservice.dto.note.NoteDetailedResponseDto
import com.kfd.noteservice.dto.note.NoteRequestDto
import com.kfd.noteservice.dto.note.NoteResponseDto
import com.kfd.noteservice.services.MessageService
import com.kfd.noteservice.services.NoteService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val noteService: NoteService,
    private val messageService: MessageService
) {

    private fun Note.toResponseDto(): NoteResponseDto = NoteResponseDto(
        id = this.id,
        title = this.title,
        body = this.body,
        favorite = this.favorite
    )
    private fun List<Note>.toResponseDtoList(): List<NoteResponseDto> = this.map { it.toResponseDto() }

    private fun Message.toResponseDto(): MessageResponseDto = MessageResponseDto(
        text = this.text,
        sender = this.sender,
    )

    private fun mapNoteToDetailedDto(note: Note) : NoteDetailedResponseDto {
        val messages = messageService.getMessages(note.noteThread!!)
        return NoteDetailedResponseDto(
            id = note.id,
            title = note.title,
            body = note.body,
            favorite = note.favorite,
            createdAt = note.createdAt,
            messages = messages.map { it.toResponseDto() }
        )
    }


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
        @RequestBody @Valid body: NoteRequestDto
    ) : ResponseEntity<NoteDetailedResponseDto> {
        val note = noteService.createNote(userId.toLong(), body)
        return ResponseEntity.ok(mapNoteToDetailedDto(note))
    }

    @PutMapping("/{id:\\d+}/set-favorite")
    fun noteSetFavorite(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
    ) : ResponseEntity<Void> {
        noteService.noteSetFavorite(id, userId.toLong())
        return ResponseEntity.ok().build()
    }

    @PutMapping("/{id:\\d+}")
    fun updateNote(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
        @RequestBody @Valid body: NoteRequestDto
    ) : ResponseEntity<NoteDetailedResponseDto> {
        val note = noteService.updateNote(id, userId.toLong(), body)
        return ResponseEntity.ok(mapNoteToDetailedDto(note))
    }

    @GetMapping("/{id:\\d+}")
    fun getNote(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
    ) : ResponseEntity<NoteDetailedResponseDto>  {
        val note = noteService.getNote(id, userId.toLong())
        return ResponseEntity.ok(mapNoteToDetailedDto(note))
    }

    @DeleteMapping("/{id:\\d+}")
    fun deleteNote(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
    ) : ResponseEntity<Void> {
        noteService.deleteNote(id, userId.toLong())
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id:\\d+}/send-message")
    fun sendMessage(
        @PathVariable("id") id: Long,
        @RequestHeader("X-User-Id") userId: String,
        @RequestBody body: MessageRequestDto
    ) : ResponseEntity<MessageResponseDto> {
        val message = noteService.createUserMessage(id, userId.toLong(), body.text)
        return ResponseEntity.ok(message.toResponseDto())
    }
}