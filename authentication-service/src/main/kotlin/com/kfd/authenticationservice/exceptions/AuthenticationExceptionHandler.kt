package com.kfd.authenticationservice.exceptions

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class AuthenticationExceptionHandler {

    private val logger = LoggerFactory.getLogger(AuthenticationExceptionHandler::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, Any>> {
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

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(
        ex: ConflictException
    ): ResponseEntity<Map<String, Any?>> {

        val responseBody = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.CONFLICT.value(),
            "error" to "Conflict",
            "message" to ex.message
        )

        return ResponseEntity.status(HttpStatus.CONFLICT).body(responseBody)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupported(
        ex: HttpRequestMethodNotSupportedException,
        request: HttpServletRequest
    ): ResponseEntity<Map<String, Any?>> {

        val path = request.requestURI.toString()
        val responseBody = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.METHOD_NOT_ALLOWED.value(),
            "error" to "Method Not Allowed",
            "message" to "Method ${ex.method} is not allowed on $path. " +
                    "Allowed: ${ex.supportedHttpMethods?.joinToString()}"
        )

        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(responseBody)
    }

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(
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
    fun handleUnauthorized(
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

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now(),
            "status" to 400,
            "error" to "Bad Request",
            "message" to "Malformed or missing request body"
        )
        return ResponseEntity.badRequest().body(body)
    }


    @ExceptionHandler(Exception::class)
    fun handleError(
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