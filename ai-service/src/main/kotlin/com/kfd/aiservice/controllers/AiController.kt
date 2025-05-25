package com.kfd.aiservice.controllers

import com.kfd.aiservice.dto.AiRequestDto
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/ai")
class AiController {

    @PostMapping("/title", consumes = [MediaType.TEXT_PLAIN_VALUE])
    fun generateTitle(@RequestBody content: String): ResponseEntity<String> {
        val newTitle = "new title"
        return ResponseEntity.ok(newTitle)
    }

    @PostMapping("/respond")
    fun getAiResponse(@RequestBody request: AiRequestDto): ResponseEntity<String> {
        val reply = "ai reply"
        return ResponseEntity.ok(reply)
    }
}