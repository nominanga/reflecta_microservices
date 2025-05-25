package com.kfd.noteservice.database.entities

import com.kfd.noteservice.enums.MessageSender
import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "messages")
class Message(
    @Column(nullable = false)
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
    val id: Long = 0

    @Column(name="created_at")
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name="updated_at")
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()
}