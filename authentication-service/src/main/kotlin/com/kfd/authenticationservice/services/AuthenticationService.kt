package com.kfd.authenticationservice.services

import com.kfd.authenticationservice.dto.auth.requests.LoginRequestDto
import com.kfd.authenticationservice.dto.auth.requests.RegistrationRequestDto
import com.kfd.authenticationservice.dto.auth.responses.AuthResponseDto
import com.kfd.authenticationservice.exceptions.ConflictException
import com.kfd.authenticationservice.exceptions.InvalidCredentialsException
import com.kfd.authenticationservice.services.clients.UserServiceClient
import feign.FeignException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val redisTokenService: RedisTokenService,
    private val userServiceClient: UserServiceClient,
) {
    companion object {
        private const val ENCODING_STRENGTH = 12
    }

    private val encoder: PasswordEncoder = BCryptPasswordEncoder(ENCODING_STRENGTH)

    fun register(request: RegistrationRequestDto): AuthResponseDto {
        if (userServiceClient.existsUserByEmail(request.email)) {
            throw ConflictException("User with email ${request.email} already exists")
        }
        val hashedPassword: String = encoder.encode(request.password)
        request.password = hashedPassword

        val user = userServiceClient.createUser(request)
        val userId = user.id

        return redisTokenService.generateTokens(userId)
    }

    fun login(request: LoginRequestDto): AuthResponseDto {
        val user =
            try {
                userServiceClient.getUserByEmail(request.email)
            } catch (e: FeignException.NotFound) {
                throw InvalidCredentialsException("Invalid email or password", e)
            }

        if (encoder.matches(request.password, user.hashedPassword)) {
            return redisTokenService.generateTokens(user.id)
        } else {
            throw InvalidCredentialsException("Invalid email or password")
        }
    }

    fun refresh(
        refreshToken: String,
        sessionId: String,
    ): AuthResponseDto = redisTokenService.refreshTokens(refreshToken, sessionId)

    fun logout(
        sessionId: String,
        userId: String,
    ) {
        redisTokenService.removeRefreshToken(sessionId, userId)
    }

    fun logoutAll(userId: String) {
        redisTokenService.removeAllRefreshTokensForUser(userId)
    }
}
