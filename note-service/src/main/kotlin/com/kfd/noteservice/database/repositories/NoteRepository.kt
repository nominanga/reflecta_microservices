package com.kfd.noteservice.database.repositories

import com.kfd.noteservice.database.entities.Note
import org.springframework.data.repository.CrudRepository

interface NoteRepository : CrudRepository<Note, Long> {
    fun findAllByUserIdOrderByUpdatedAtDesc(userId: Long): List<Note>
    fun findAllByUserIdAndFavoriteTrueOrderByUpdatedAtDesc(userId: Long): List<Note>
}