package com.kfd.userservice.database.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "`users`")
class User (

    @Column(nullable = false)
    var username: String,

    @Column(unique=true, nullable = false)
    val email: String,

    @Column(nullable = false)
    var password: String,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0

    @Column(name="created_at")
    @CreationTimestamp
    val createdAt: LocalDateTime = LocalDateTime.now()

    @Column(name="updated_at")
    @UpdateTimestamp
    var updatedAt: LocalDateTime = LocalDateTime.now()

    // TODO return times of user creation and update

    var avatar: String = "/media/avatars/default.png"

    @OneToOne(mappedBy = "user", cascade = [(CascadeType.ALL)], orphanRemoval = true)
    var userSettings: UserSettings? = null
}