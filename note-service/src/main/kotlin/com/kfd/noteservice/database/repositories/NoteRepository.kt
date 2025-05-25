package com.kfd.noteservice.database.repositories

import com.kfd.noteservice.database.entities.Note
import org.springframework.data.repository.CrudRepository

interface NoteRepository : CrudRepository<Note, Long> {
    fun findAllByUserIdOrderByCreatedAtDesc(userId: Long): List<Note>
    fun findAllByUserIdAndFavoriteTrueOrderByCreatedAtDesc(userId: Long): List<Note>
}