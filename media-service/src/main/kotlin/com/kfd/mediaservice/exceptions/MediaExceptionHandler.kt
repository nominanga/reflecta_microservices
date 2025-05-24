package com.kfd.mediaservice.exceptions

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class MediaExceptionHandler {

    private val logger = LoggerFactory.getLogger(MediaExceptionHandler::class.java)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(
        ex: IllegalArgumentException
    ): ResponseEntity<Map<String, Any?>> {
        val body = mapOf(
            "timestamp" to Instant.now(),
            "status" to 400,
            "error" to "Bad Request",
            "message" to ex.message
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