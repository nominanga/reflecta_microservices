package com.kfd.noteservice.database.entities

import com.kfd.noteservice.enums.MessageSender
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "messages")
class Message(
    @Column(nullable = false, columnDefinition = "TEXT")
    var text: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val sender: MessageSender = MessageSender.AI,
    @ManyToOne(optional = false)
    @JoinColumn(name = "thread_id")
    val noteThread: NoteThread,
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
}
