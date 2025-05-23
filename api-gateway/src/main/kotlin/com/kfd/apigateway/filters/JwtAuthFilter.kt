package com.kfd.apigateway.filters

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.GatewayFilterChain
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.*
import java.time.Instant

@Component
class JwtAuthFilter(
    @Value("\${jwt.access-secret}") private val base64Secret: String,
    private val objectMapper: ObjectMapper
) : GatewayFilter, Ordered {

//    @PostConstruct
//    fun testTokenValidation() {
//        logger.info("ACCESS_SECRET is {}", base64Secret)
//        val now = System.currentTimeMillis()
//
//        val token = Jwts.builder()
//            .claim("id", "42")
//            .issuedAt(Date(now))
//            .expiration(Date(now + 3600000)) // 1 hour
//            .signWith(secret)
//            .compact()
//
//        logger.info("Generated token: {}", token)
//
//        try {
//            val claims = Jwts.parser()
//                .verifyWith(secret)
//                .build()
//                .parseSignedClaims(token)
//                .payload
//
//            logger.info("Parsed claims: {}", claims)
//
//            val userId = claims["id"]?.toString()
//
//            logger.info("Parsed user id {} should equal to 42", userId)
//        } catch (e: Exception) {
//            logger.info("Problem with tokens in PostConstruct: {}", e.message)
//        }
//    }

    private val logger = LoggerFactory.getLogger(JwtAuthFilter::class.java)

    private val secret = Keys.hmacShaKeyFor(Base64.getDecoder().decode(base64Secret))

    override fun filter(exchange: ServerWebExchange, chain: GatewayFilterChain): Mono<Void> {
        val request = exchange.request
        val authHeader = request.headers.getFirst(HttpHeaders.AUTHORIZATION)

        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            return unauthorizedResponse(exchange, "Missing or invalid Authorization header")
        }

        val token = authHeader.removePrefix("Bearer ").trim()
        logger.info("Incoming token: {}", token)

        return try {
            val claims = Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .payload

            logger.info("Parsed JWT claims: {}", claims)

            val userId = claims["id"]?.toString()
            if (userId.isNullOrBlank()) {
                unauthorizedResponse(exchange, "User ID missing in token")
            } else {
                val mutatedRequest = request.mutate()
                    .header("X-User-Id", userId)
                    .build()

                val mutatedExchange = exchange.mutate().request(mutatedRequest).build()
                chain.filter(mutatedExchange)
            }

        } catch (e: ExpiredJwtException) {
            logger.info("Token has expired: {}", e.message)
            unauthorizedResponse(exchange, "Token has expired")
        } catch (e: Exception) {
            logger.info("Invalid token: {}", e.message)
            unauthorizedResponse(exchange, "Invalid token")
        }
    }

    override fun getOrder(): Int = -1

    private fun unauthorizedResponse(exchange: ServerWebExchange, message: String): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON

        val bodyMap = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.UNAUTHORIZED.value(),
            "error" to "Unauthorized",
            "message" to message,
        )

        val json = objectMapper.writeValueAsString(bodyMap)
        val buffer = response.bufferFactory().wrap(json.toByteArray(StandardCharsets.UTF_8))
        return response.writeWith(Mono.just(buffer))
    }
}