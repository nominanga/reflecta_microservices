package com.kfd.userservice.services

import com.kfd.userservice.database.repositories.UserRepository
import com.kfd.userservice.database.entities.User
import com.kfd.userservice.database.entities.UserSettings
import com.kfd.userservice.dto.requests.RegistrationRequestDto
import com.kfd.userservice.dto.requests.UserUpdateDto
import jakarta.persistence.EntityNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    private val encoder: PasswordEncoder = BCryptPasswordEncoder(12)

    fun createUser(userCredentials: RegistrationRequestDto): User {

        val user = User(
            username = userCredentials.username,
            email = userCredentials.email,
            password = userCredentials.password
        )

        user.userSettings = UserSettings(
            user = user
        )

        return userRepository.save(user)
    }

    fun getUser(id: Long): User {
        val user = userRepository.findById(id).orElseThrow {
            EntityNotFoundException("User with id: $id not found")
        }
        return user
    }

    fun deleteUser(id: Long) {
        val user = getUser(id)
        // TODO request to authentication-service to delete all user associated tokens
        userRepository.delete(user)
    }


    fun existsUserByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    fun getUserByEmail(email: String): User {
        val user = userRepository.findByEmail(email) ?: throw EntityNotFoundException("User with email: $email not found")
        return user
    }

    fun updateUser(id: Long, userData: UserUpdateDto): User {
        val user = getUser(id)
        userData.username?.let { user.username = it }
        userData.newPassword?.let {
            val hashedPassword = encoder.encode(it)
            user.password = hashedPassword
        }

        userData.notificationFrequency?.let { user.userSettings?.notificationFrequency = it}
        userData.cachedReportsAmount?.let { user.userSettings?.cachedReportsAmount = it}
        userData.allowStatisticsNotify?.let { user.userSettings?.allowStatisticsNotify = it}

        return userRepository.save(user)
    }

    fun updateAvatar(id: Long, file: MultipartFile): User {

        // TODO create media-service where avatar upload would be handled
        val user = getUser(id)

        if (file.isEmpty) throw IllegalArgumentException("File is empty")
        if (!file.contentType?.startsWith("image/")!!) throw IllegalArgumentException("Not an Image")

        val uploadDir = Paths.get("uploads/avatars")
        Files.createDirectories(uploadDir)

        val extension = file.originalFilename?.substringAfterLast('.', "") ?: "jpg"
        val filename = "$id.$extension"
        val filePath = uploadDir.resolve(filename)

        file.inputStream.use { input -> Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING) }

        user.avatar = "/media/avatars/$filename"
        return userRepository.save(user)
    }

}