package com.kfd.noteservice.database.repositories

import com.kfd.noteservice.database.entities.Message
import com.kfd.noteservice.database.entities.NoteThread
import org.springframework.data.repository.CrudRepository

interface MessageRepository : CrudRepository<Message, Long> {
    fun findAllByNoteThreadOrderByCreatedAtAsc(noteThread: NoteThread): List<Message>
    fun deleteAllByNoteThread(noteThread: NoteThread)
}