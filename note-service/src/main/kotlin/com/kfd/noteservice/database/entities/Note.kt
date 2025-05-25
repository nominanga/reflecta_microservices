package com.kfd.noteservice.database.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "notes")
class Note(
    @Column
    var title: String?,

    @Column
    var body: String,

    @Column(name = "user_id")
    val userId: Long
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(name="created_at")
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name="updated_at")
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "is_favorite")
    var isFavorite: Boolean = false

    @OneToOne(mappedBy = "note", cascade = [CascadeType.ALL], orphanRemoval = true)
    var noteThread: NoteThread? = null
}