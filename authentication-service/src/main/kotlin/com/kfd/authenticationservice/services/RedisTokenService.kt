package com.kfd.authenticationservice.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kfd.authenticationservice.dtos.auth.responses.AuthResponse
import com.kfd.authenticationservice.exceptions.UnauthorizedException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class RedisTokenService (
    private val objectMapper: ObjectMapper,
    private val redisTemplate: StringRedisTemplate,
    private val jwtGenerationService: JwtGenerationService,
) {

    private data class RefreshTokenPayload (
        val userId: String,
        val sessionId: String
    )

    fun generateTokens(userId: String): AuthResponse {

        val accessToken = jwtGenerationService.generateAccessToken(userId)
        val refreshToken = jwtGenerationService.generateRefreshToken(userId)

        val sessionId = UUID.randomUUID().toString()

        val value = RefreshTokenPayload(
            userId = userId,
            sessionId = sessionId
        )

        redisTemplate.opsForValue().set(
            "refresh:$refreshToken",
            objectMapper.writeValueAsString(value),
            Duration.ofSeconds(jwtGenerationService.refreshTokenExpirationTime() / 1000)
        )

        return AuthResponse(
            accessToken,
            refreshToken,
            sessionId
        )
    }

    fun removeRefreshToken(refreshToken: String, userId: String?) {
        val stored = redisTemplate.opsForValue().get("refresh:$refreshToken")
            ?: throw UnauthorizedException("Refresh token is expired or invalid")
        val data = objectMapper.readValue(stored, RefreshTokenPayload::class.java)
        if (data.userId != userId) {
            throw UnauthorizedException("User is unauthorized")
        }
        redisTemplate.delete("refresh:$refreshToken")
    }

    fun refreshTokens(refreshToken: String, sessionId: String): AuthResponse {
        val stored = redisTemplate.opsForValue().get("refresh:$refreshToken")
            ?: throw UnauthorizedException("Refresh token is expired or invalid")

        val data = objectMapper.readValue(stored, RefreshTokenPayload::class.java)
        if (data.sessionId != sessionId) {
            throw UnauthorizedException("Wrong session id")
        }
        val userId = data.userId

        removeRefreshToken(refreshToken, userId)
        return generateTokens(userId)
    }
}