package com.kfd.mediaservice.controllers

import com.kfd.mediaservice.dto.UploadAvatarResponseDto
import com.kfd.mediaservice.services.MediaService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping
class MediaController(
    private val mediaService: MediaService,
) {
    @PostMapping("api/me/avatar", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadAvatar(
        @RequestHeader("X-User-Id") userId: String,
        @RequestPart("file") file: MultipartFile,
    ): ResponseEntity<UploadAvatarResponseDto> {
        val uri = mediaService.uploadAvatar(file, userId)
        val response = UploadAvatarResponseDto(uri = uri)
        return ResponseEntity.ok(response)
    }
}
