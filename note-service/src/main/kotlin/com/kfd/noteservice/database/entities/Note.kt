package com.kfd.noteservice.database.entities

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "notes")
class Note(
    @Column
    var title: String?,
    @Column(columnDefinition = "TEXT")
    var body: String,
    @Column(name = "user_id")
    val userId: Long,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name = "created_at")
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "updated_at")
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()

    @Column(name = "favorite")
    var favorite: Boolean = false

    @OneToOne(mappedBy = "note", cascade = [CascadeType.ALL], orphanRemoval = true)
    var noteThread: NoteThread? = null
}
