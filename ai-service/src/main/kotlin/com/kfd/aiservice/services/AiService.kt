package com.kfd.aiservice.services

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class AiService(
    @Value("\${ai.api-key}") private val aiApiKey: String,
) {

}