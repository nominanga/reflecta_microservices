package com.kfd.authenticationservice.services

import com.kfd.authenticationservice.dtos.auth.requests.LoginRequest
import com.kfd.authenticationservice.dtos.auth.requests.RegistrationRequest
import com.kfd.authenticationservice.dtos.auth.responses.AuthResponse
import com.kfd.authenticationservice.services.clients.UserServiceClient
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val redisTokenService: RedisTokenService,
    private val userServiceClient: UserServiceClient
) {

    private val encoder: PasswordEncoder = BCryptPasswordEncoder(12)

    fun register(request: RegistrationRequest): AuthResponse {
        if (userServiceClient.existsUserByEmail(request.email)) {
            throw Exception("User with email ${request.email} already exists")
        }
        val hashedPassword: String = encoder.encode(request.password)
        request.password = hashedPassword

        val user = userServiceClient.createUser(request)
        val userId = user.id

        return redisTokenService.generateTokens(userId)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userServiceClient.getUserByEmail(request.email)

        if (encoder.matches(request.password, user.hashedPassword)) {
            return redisTokenService.generateTokens(user.id)
        } else {
            throw Exception("Wrong password")
        }
    }

    fun refresh(refreshToken: String, sessionId: String): AuthResponse =
        redisTokenService.refreshTokens(refreshToken, sessionId)

    fun logout(refreshToken: String) {
        redisTokenService.removeRefreshToken(refreshToken)
    }

}