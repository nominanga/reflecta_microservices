package com.kfd.noteservice.database.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "note_threads")
class NoteThread(
    @OneToOne
    @JoinColumn(name = "note_id")
    val note: Note,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(
        mappedBy = "noteThread",
        cascade = [(CascadeType.ALL)],
        orphanRemoval = true,
    )
    val messages: MutableList<Message> = mutableListOf()
}
