package com.kfd.userservice.database.entities

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
@Table(name = "`users`")
class User(
    @Column(nullable = false)
    var username: String,
    @Column(unique = true, nullable = false)
    val email: String,
    @Column(nullable = false)
    var password: String,
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

    // TODO return times of user creation and update

    @Column
    var avatar: String = "/media/avatars/default.png"

    @OneToOne(mappedBy = "user", cascade = [(CascadeType.ALL)], orphanRemoval = true)
    var userSettings: UserSettings? = null
}
