package com.kfd.userservice.dto.responses


data class UserResponseDto(
    val username: String,
    val email: String,
    val avatar: String,
    val notificationFrequency: Int?,
    val allowStatisticsNotify: Boolean?,
    val cachedReportsAmount: Int?
)