package com.kfd.authenticationservice.controllers

import com.kfd.authenticationservice.dto.auth.requests.LoginRequestDto
import com.kfd.authenticationservice.dto.auth.requests.LogoutRequestDto
import com.kfd.authenticationservice.dto.auth.requests.RefreshRequestDto
import com.kfd.authenticationservice.dto.auth.requests.RegistrationRequestDto
import com.kfd.authenticationservice.dto.auth.responses.AuthResponseDto
import com.kfd.authenticationservice.services.AuthenticationService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationService: AuthenticationService
) {

    @PostMapping("/register")
    fun register(
        @RequestBody @Valid requestBody: RegistrationRequestDto
    ) : ResponseEntity<AuthResponseDto> {
        val tokenPair = authenticationService.register(requestBody)
        return ResponseEntity.ok(tokenPair)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid requestBody: LoginRequestDto
    ) : ResponseEntity<AuthResponseDto> {
        val tokenPair = authenticationService.login(requestBody)
        return ResponseEntity.ok(tokenPair)
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody body: LogoutRequestDto,
        @RequestHeader("X-User-Id", required = false) userId: String?
    ) : ResponseEntity<Void> {
        authenticationService.logout(body.refreshToken, userId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/refresh")
    fun refreshAccessToken(
        @RequestBody body: RefreshRequestDto
    ) : ResponseEntity<AuthResponseDto> {
        val tokenPair = authenticationService.refresh(body.refreshToken, body.sessionId)
        return ResponseEntity.ok(tokenPair)
    }

}