package com.kfd.authenticationservice.services

import com.fasterxml.jackson.databind.ObjectMapper
import com.kfd.authenticationservice.dtos.auth.responses.AuthResponse
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class RedisTokenService (
    private val objectMapper: ObjectMapper,
    private val redisTemplate: StringRedisTemplate,
    private val jwtGenerationService: JwtGenerationService,
) {
    fun generateTokens(userId: Long): AuthResponse {

        val accessToken = jwtGenerationService.generateAccessToken(userId)
        val refreshToken = jwtGenerationService.generateRefreshToken(userId)

        val value = mapOf(
            "id" to userId,
            "issuedAt" to System.currentTimeMillis() / 1000,
            "expiresAt" to (System.currentTimeMillis()
                    + jwtGenerationService.refreshTokenExpirationTime()) / 1000
        )

        redisTemplate.opsForValue().set(
            refreshToken,
            objectMapper.writeValueAsString(value),
            Duration.ofSeconds(jwtGenerationService.refreshTokenExpirationTime() / 1000)
        )

        return AuthResponse(
            accessToken,
            refreshToken
        )
    }

    fun removeRefreshToken(refreshToken: String) {
        redisTemplate.delete(refreshToken)
    }

    fun refreshTokens(refreshToken: String): AuthResponse {
        val stored = redisTemplate.opsForValue().get(refreshToken)
            ?: throw Exception("Refresh token is expired or invalid")

        val data = objectMapper.readValue(stored, Map::class.java)
        val userId = data["id"] as Long

        removeRefreshToken(refreshToken)
        return generateTokens(userId)
    }
}