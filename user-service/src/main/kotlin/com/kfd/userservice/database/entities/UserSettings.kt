package com.kfd.userservice.database.entities

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user_settings")
class UserSettings(

    @Column(name="notification_frequency")
    var notificationFrequency: Int = 10,

    @Column(name="allow_statistics_notify")
    var allowStatisticsNotify: Boolean = false,

    @Column(name="cached_reports_amount")
    var cachedReportsAmount: Int = 100,

    @OneToOne
    @JoinColumn(name = "user_id")
    val user: User
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