package com.kfd.userservice.dto.requests

import jakarta.validation.constraints.*

data class UserUpdateDto(
    @field:NotEmpty(message = "Username cannot be empty")
    @field:Size(max = 20, message = "Username cannot be longer than 20 characters")
    val username: String?,

    @field:Min(1, message = "Notification frequency have to be at least 1")
    @field:Max(5, message = "Notification frequency have to be less or equal to 5")
    val notificationFrequency: Int?,

    val allowStatisticsNotify: Boolean?,

    @field:Min(1, message = "At least oe cached report have to be set")
    @field:Max(100, message = "Cannot store more than 100 cached reports")
    val cachedReportsAmount: Int?,

    @field:Size(min = 8, max = 20, message = "Password must be from 8 to 20 characters long")
    @field:Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+\$",
        message = "Password must contain at least one lowercase, one uppercase and one digit"
    )
    val newPassword: String?
)
