package com.kfd.noteservice.exceptions

import feign.FeignException
import jakarta.persistence.EntityNotFoundException
import jakarta.servlet.http.HttpServletRequest
import jakarta.ws.rs.ForbiddenException
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
class NoteExceptionHandler {

    private val logger = LoggerFactory.getLogger(NoteExceptionHandler::class.java)

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

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleUnreadable(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Bad Request",
            "message" to "Malformed or missing request body"
        )
        return ResponseEntity.badRequest().body(body)
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

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.NOT_FOUND.value(),
            "error" to "Not Found",
            "message" to ex.message
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(ex: ForbiddenException): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.FORBIDDEN.value(),
            "error" to "Forbidden",
            "message" to ex.message
        )
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Bad Request",
            "message" to ex.message
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body)
    }

    @ExceptionHandler(FeignException::class)
    fun handleFeignException(ex: FeignException): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now(),
            "status" to HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error" to "Internal Server Error",
            "message" to "Something went wrong with DeepSeek API. Try again later"
        )
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body)
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