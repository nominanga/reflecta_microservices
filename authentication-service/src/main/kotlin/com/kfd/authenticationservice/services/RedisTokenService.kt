package com.kfd.authenticationservice.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kfd.authenticationservice.dto.auth.responses.AuthResponseDto
import com.kfd.authenticationservice.exceptions.UnauthorizedException
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.UUID

@Service
class RedisTokenService(
    private val objectMapper: ObjectMapper,
    private val redisTemplate: StringRedisTemplate,
    private val jwtGenerationService: JwtGenerationService,
) {
    companion object {
        private const val MILLISECONDS_IN_SECOND = 1000
    }

    private data class RefreshTokenPayload(
        val userId: String,
        val refreshToken: String,
    )

    fun cleanupExpiredSessionsForUser(userId: String) {
        val userSessionsKey = "user_sessions:$userId"
        val sessionKeys = redisTemplate.opsForSet().members(userSessionsKey) ?: return

        sessionKeys.forEach { key ->
            if (!redisTemplate.hasKey(key)) {
                redisTemplate.opsForSet().remove(userSessionsKey, key)
            }
        }
    }

    fun generateTokens(userId: String): AuthResponseDto {
        val accessToken = jwtGenerationService.generateAccessToken(userId)
        val refreshToken = jwtGenerationService.generateRefreshToken(userId)
        val sessionId = UUID.randomUUID().toString()

        val payload =
            RefreshTokenPayload(
                userId = userId,
                refreshToken = refreshToken,
            )

        val sessionKey = "session:$sessionId"
        val userSessionsKey = "user_sessions:$userId"
        val duration = Duration.ofMillis(jwtGenerationService.refreshTokenExpirationTime())

        redisTemplate.opsForValue().set(
            sessionKey,
            objectMapper.writeValueAsString(payload),
            duration,
        )

        cleanupExpiredSessionsForUser(userId)
        redisTemplate.opsForSet().add(userSessionsKey, sessionKey)

        return AuthResponseDto(
            accessToken,
            refreshToken,
            sessionId,
        )
    }

    fun removeRefreshToken(
        sessionId: String,
        userId: String,
    ) {
        val sessionKey = "session:$sessionId"
        val stored =
            redisTemplate.opsForValue().get(sessionKey)
                ?: throw UnauthorizedException("Refresh token is expired or invalid")

        val data = objectMapper.readValue(stored, RefreshTokenPayload::class.java)
        if (data.userId != userId) {
            throw UnauthorizedException("User is unauthorized")
        }

        redisTemplate.delete(sessionKey)
        cleanupExpiredSessionsForUser(userId)
    }

    fun refreshTokens(
        refreshToken: String,
        sessionId: String,
    ): AuthResponseDto {
        val sessionKey = "session:$sessionId"
        val stored =
            redisTemplate.opsForValue().get(sessionKey)
                ?: throw UnauthorizedException("Session is no longer active")

        val data = objectMapper.readValue(stored, RefreshTokenPayload::class.java)
        if (data.refreshToken != refreshToken) {
            throw UnauthorizedException("Invalid refresh token")
        }

        val newAccessToken = jwtGenerationService.generateAccessToken(data.userId)
        val newRefreshToken = jwtGenerationService.generateRefreshToken(data.userId)

        val updatedPayload =
            RefreshTokenPayload(
                userId = data.userId,
                refreshToken = newRefreshToken,
            )

        redisTemplate.opsForValue().set(
            sessionKey,
            objectMapper.writeValueAsString(updatedPayload),
            Duration.ofSeconds(
                jwtGenerationService.refreshTokenExpirationTime() /
                    MILLISECONDS_IN_SECOND,
            ),
        )

        return AuthResponseDto(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
            sessionId = sessionId,
        )
    }

    fun removeAllRefreshTokensForUser(userId: String) {
        val userSessionsKey = "user_sessions:$userId"

        cleanupExpiredSessionsForUser(userId)

        val keys = redisTemplate.opsForSet().members(userSessionsKey) ?: emptySet()

        if (keys.isNotEmpty()) {
            redisTemplate.delete(keys)
        }
        redisTemplate.delete(userSessionsKey)
    }
}
