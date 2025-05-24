package com.kfd.userservice.controllers

import com.kfd.userservice.database.entities.User
import com.kfd.userservice.dto.requests.UserUpdateDto
import com.kfd.userservice.dto.responses.UserResponseDto
import com.kfd.userservice.services.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/me")
class UserController(
    private val userService: UserService,
) {

    private fun mapToUserDto(user: User) = UserResponseDto(
        username = user.username,
        email = user.email,
        avatar = user.avatar,
        notificationFrequency = user.userSettings!!.notificationFrequency,
        allowStatisticsNotify = user.userSettings!!.allowStatisticsNotify,
        cachedReportsAmount = user.userSettings!!.cachedReportsAmount
    )


    @GetMapping
    fun getUser(
        @RequestHeader("X-User-Id") userId: String,
    ) : ResponseEntity<UserResponseDto> {
        val user = userService.getUser(userId.toLong())
        return ResponseEntity.ok(mapToUserDto(user))
    }

    @PutMapping
    fun updateUser(
        @RequestHeader("X-User-Id") userId: String,
        @RequestBody @Valid requestBody: UserUpdateDto
    ) : ResponseEntity<UserResponseDto> {
        val user = userService.updateUser(userId.toLong(), requestBody)
        return ResponseEntity.ok(mapToUserDto(user))
    }

    @PutMapping("/avatar")
    fun updateAvatar(
        @RequestHeader("X-User-Id") userId: String,
        @RequestParam avatar: MultipartFile
    ) : ResponseEntity<UserResponseDto> {
        val user = userService.updateAvatar(userId.toLong(), avatar)
        return ResponseEntity.ok(mapToUserDto(user))
    }

    @DeleteMapping
    fun deleteUser(
        @RequestHeader("X-User-Id") userId: String
    ) : ResponseEntity<Void> {
        userService.deleteUser(userId.toLong())
        return ResponseEntity.noContent().build()
    }
}