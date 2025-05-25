package com.kfd.noteservice.database.entities

import jakarta.persistence.*

@Entity
@Table(name = "note_threads")
class NoteThread(
    @OneToOne
    @JoinColumn(name = "note_id")
    val note: Note
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @OneToMany(
        mappedBy = "noteThread",
        cascade = [(CascadeType.ALL)],
        orphanRemoval = true
    )
    val messages: MutableList<Message> = mutableListOf()
}