package com.kfd.userservice.controllers

import com.kfd.userservice.database.entities.User
import com.kfd.userservice.dto.requests.RegistrationRequestDto
import com.kfd.userservice.dto.requests.UserAvatarUpdateDto
import com.kfd.userservice.dto.responses.UserAuthenticationResponseDto
import com.kfd.userservice.services.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/user")
class UserInternalController(
    private val userService: UserService,
) {
    fun mapUserToResponse(user: User): UserAuthenticationResponseDto {
        return UserAuthenticationResponseDto(
            id = user.id.toString(),
            hashedPassword = user.password,
        )
    }

    @PostMapping
    fun createUser(
        @RequestBody body: RegistrationRequestDto,
    ): ResponseEntity<UserAuthenticationResponseDto> {
        return ResponseEntity.ok(mapUserToResponse(userService.createUser(body)))
    }

    @GetMapping("/email")
    fun getUserByEmail(
        @RequestParam(value = "email") email: String,
    ): ResponseEntity<UserAuthenticationResponseDto> {
        return ResponseEntity.ok(mapUserToResponse(userService.getUserByEmail(email)))
    }

    @GetMapping("/email/exists")
    fun existsUserByEmail(
        @RequestParam(value = "email") email: String,
    ): ResponseEntity<Boolean> {
        return ResponseEntity.ok(userService.existsUserByEmail(email))
    }

    @PutMapping("/update/avatar")
    fun updateAvatar(
        @RequestBody body: UserAvatarUpdateDto,
    ): ResponseEntity<Void> {
        userService.updateAvatar(body.userId.toLong(), body.uri)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}/username")
    fun getUsername(
        @PathVariable("id") id: Long,
    ): ResponseEntity<String> {
        val user = userService.getUser(id)
        return ResponseEntity.ok(user.username)
    }
}
