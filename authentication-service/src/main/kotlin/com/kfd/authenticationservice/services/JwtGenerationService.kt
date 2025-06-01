package com.kfd.authenticationservice.services

import com.kfd.authenticationservice.configuration.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Base64
import java.util.Date

@Service
class JwtGenerationService(private val jwtProperties: JwtProperties) {
    private val accessSecret =
        Keys.hmacShaKeyFor(
            Base64.getDecoder().decode(jwtProperties.access.secret),
        )
    private val refreshSecret =
        Keys.hmacShaKeyFor(
            Base64.getDecoder().decode(jwtProperties.refresh.secret),
        )

    fun generateToken(
        userId: String,
        isAccess: Boolean,
    ): String {
        val now = System.currentTimeMillis()
        val (expiration, secret) =
            if (isAccess) {
                jwtProperties.access.expirationTime to accessSecret
            } else {
                jwtProperties.refresh.expirationTime to refreshSecret
            }

        return Jwts.builder()
            .claim("id", userId)
            .issuedAt(Date(now))
            .expiration(Date(now + expiration))
            .signWith(secret)
            .compact()
    }

    fun generateAccessToken(userId: String): String = generateToken(userId, isAccess = true)

    fun generateRefreshToken(userId: String): String = generateToken(userId, isAccess = false)

    fun refreshTokenExpirationTime(): Long = jwtProperties.refresh.expirationTime
}
