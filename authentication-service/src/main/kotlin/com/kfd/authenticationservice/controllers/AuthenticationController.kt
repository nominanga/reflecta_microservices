package com.kfd.authenticationservice.controllers

import com.kfd.authenticationservice.dtos.auth.requests.LoginRequest
import com.kfd.authenticationservice.dtos.auth.requests.LogoutRequest
import com.kfd.authenticationservice.dtos.auth.requests.RefreshRequest
import com.kfd.authenticationservice.dtos.auth.requests.RegistrationRequest
import com.kfd.authenticationservice.dtos.auth.responses.AuthResponse
import com.kfd.authenticationservice.exceptions.BadRequestException
import com.kfd.authenticationservice.exceptions.UnauthorizedException
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
        @RequestBody @Valid requestBody: RegistrationRequest
    ) : ResponseEntity<AuthResponse> {
        val tokenPair = authenticationService.register(requestBody)
        return ResponseEntity.ok(tokenPair)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody @Valid requestBody: LoginRequest
    ) : ResponseEntity<AuthResponse> {
        val tokenPair = authenticationService.login(requestBody)
        return ResponseEntity.ok(tokenPair)
    }

    @PostMapping("/logout")
    fun logout(
        @RequestBody body: LogoutRequest,
        @RequestHeader("X-User-Id", required = false) userId: String?
    ) : ResponseEntity<Void> {
        authenticationService.logout(body.refreshToken, userId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/refresh")
    fun refreshAccessToken(
        @RequestBody body: RefreshRequest
    ) : ResponseEntity<AuthResponse> {
        val tokenPair = authenticationService.refresh(body.refreshToken, body.sessionId)
        return ResponseEntity.ok(tokenPair)
    }

}