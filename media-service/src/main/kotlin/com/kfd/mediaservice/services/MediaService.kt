package com.kfd.mediaservice.services

import com.kfd.mediaservice.dto.UserAvatarUpdateDto
import com.kfd.mediaservice.services.clients.UserClientService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Service
class MediaService(
    private val userClientService: UserClientService
) {
    fun uploadAvatar(file: MultipartFile, userId: String) : String {
        if (file.isEmpty) throw IllegalArgumentException("File can not be empty")

        val contentType = file.contentType
        if (contentType == null || !contentType.startsWith("image/")) {
            throw IllegalArgumentException("File must be an image")
        }


        val uploadDir = Paths.get("/media/avatars")
        Files.createDirectories(uploadDir)

        val extension = file.originalFilename?.substringAfterLast(".") ?: "jpg"
        val filename = "$userId.$extension"
        val filePath = uploadDir.resolve(filename)

        file.inputStream.use { input -> Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING) }

        val uri =  "/media/avatars/$filename"
        userClientService.updateAvatar(UserAvatarUpdateDto(
            userId = userId,
            uri = uri
        ))
        return uri
    }
}