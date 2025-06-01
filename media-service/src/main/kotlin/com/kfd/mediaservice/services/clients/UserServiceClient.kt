package com.kfd.mediaservice.services.clients

import com.kfd.mediaservice.dto.UserAvatarUpdateDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "user-service")
interface UserServiceClient {
    @PutMapping("/internal/user/update/avatar")
    fun updateAvatar(
        @RequestBody body: UserAvatarUpdateDto,
    ): Unit
}
