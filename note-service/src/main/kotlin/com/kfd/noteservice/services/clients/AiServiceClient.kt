package com.kfd.noteservice.services.clients

import com.kfd.noteservice.dto.ai.AiRequestDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(name = "ai-service")
interface AiServiceClient {
    @PostMapping("/ai/title")
    fun generateTitle(
        @RequestBody content: String,
    ): String

    @PostMapping("/ai/respond")
    fun getAiResponse(
        @RequestBody body: AiRequestDto,
    ): String
}
