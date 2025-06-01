package com.kfd.authenticationservice.exceptions

class InvalidCredentialsException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class UnauthorizedException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class ConflictException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
