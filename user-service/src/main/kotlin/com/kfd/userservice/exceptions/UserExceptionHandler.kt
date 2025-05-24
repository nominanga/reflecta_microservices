package com.kfd.userservice.exceptions

import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class UserExceptionHandler {

    private val logger = LoggerFactory.getLogger(UserExceptionHandler::class.java)

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
            "status" to 400,
            "error" to "Bad Request",
            "message" to "Malformed or missing request body"
        )
        return ResponseEntity.badRequest().body(body)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleNotFound(ex: EntityNotFoundException): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now(),
            "status" to 400,
            "error" to "Bad Request",
            "message" to ex.message
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body)
    }


    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParam(ex: MissingServletRequestParameterException): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now(),
            "status" to 400,
            "error" to "Bad Request",
            "message" to "Missing query parameter: ${ex.parameterName}"
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