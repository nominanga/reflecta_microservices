package com.kfd.authenticationservice.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class AuthenticationExceptionHandler {

    private val logger = LoggerFactory.getLogger(AuthenticationExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
        val errors = ex.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "Invalid value") }

        val responseBody = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Bad request",
            "message" to "Validation failed",
            "fields" to errors
        )

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody)
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentialsException(
        ex: InvalidCredentialsException
    ): ResponseEntity<Map<String, Any?>> {

        val responseBody = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.UNAUTHORIZED.value(),
            "error" to "Unauthorized",
            "message" to ex.message
        )

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(
        ex: UnauthorizedException
    ): ResponseEntity<Map<String, Any?>> {

        val responseBody = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.UNAUTHORIZED.value(),
            "error" to "Unauthorized",
            "message" to ex.message
        )

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody)
    }

    @ExceptionHandler(Exception::class)
    fun handleInternalException(
        ex: Exception
    ): ResponseEntity<Map<String, Any?>> {

        logger.error("Exception occurred: ${ex.message}", ex)

        val responseBody = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to "Internal Server Error",
            "message" to "Something went wrong",
        )

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody)
    }


}