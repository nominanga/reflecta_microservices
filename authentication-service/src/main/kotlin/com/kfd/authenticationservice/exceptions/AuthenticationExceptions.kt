package com.kfd.authenticationservice.exceptions

class InvalidCredentialsException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String) : RuntimeException(message)
class BadRequestException(message: String) : RuntimeException(message)
class ConflictException(message: String) : RuntimeException(message)