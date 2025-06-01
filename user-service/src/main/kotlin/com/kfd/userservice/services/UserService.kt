package com.kfd.userservice.services

import com.kfd.userservice.database.entities.User
import com.kfd.userservice.database.entities.UserSettings
import com.kfd.userservice.database.repositories.UserRepository
import com.kfd.userservice.dto.requests.RegistrationRequestDto
import com.kfd.userservice.dto.requests.UserUpdateDto
import com.kfd.userservice.services.clients.AuthenticationServiceClient
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authenticationServiceClient: AuthenticationServiceClient,
) {
    companion object {
        private const val ENCODING_STRENGTH = 12
    }

    private val encoder: PasswordEncoder = BCryptPasswordEncoder(ENCODING_STRENGTH)

    @Transactional
    fun createUser(userCredentials: RegistrationRequestDto): User {
        val user =
            User(
                username = userCredentials.username,
                email = userCredentials.email,
                password = userCredentials.password,
            )

        user.userSettings =
            UserSettings(
                user = user,
            )

        return userRepository.save(user)
    }

    fun getUser(id: Long): User {
        val user =
            userRepository.findById(id).orElseThrow {
                EntityNotFoundException("User with id: $id not found")
            }
        return user
    }

    @Transactional
    fun deleteUser(id: Long) {
        val user = getUser(id)
        authenticationServiceClient.logoutAll(id.toString())
        userRepository.delete(user)
    }

    fun existsUserByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    fun getUserByEmail(email: String): User {
        val user =
            userRepository.findByEmail(email)
                ?: throw EntityNotFoundException("User with email: $email not found")
        return user
    }

    @Transactional
    fun updateUser(
        id: Long,
        userData: UserUpdateDto,
    ): User {
        val user = getUser(id)
        userData.username?.let { user.username = it }
        userData.newPassword?.let {
            val hashedPassword = encoder.encode(it)
            user.password = hashedPassword
        }

        userData.notificationFrequency?.let { user.userSettings?.notificationFrequency = it }
        userData.cachedReportsAmount?.let { user.userSettings?.cachedReportsAmount = it }
        userData.allowStatisticsNotify?.let { user.userSettings?.allowStatisticsNotify = it }

        return userRepository.save(user)
    }

    @Transactional
    fun updateAvatar(
        id: Long,
        mediaUri: String,
    ): User {
        val user = getUser(id)
        user.avatar = mediaUri
        return userRepository.save(user)
    }
}
