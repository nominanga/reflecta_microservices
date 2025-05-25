package com.kfd.aiservice.services

import com.kfd.aiservice.chat.ChatRequest
import com.kfd.aiservice.chat.ChatResponse
import com.kfd.aiservice.dto.AiMessageDto
import com.kfd.aiservice.dto.AiRequestDto
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient

@Service
class AiService(
    @Value("\${ai.api-key}") private val aiApiKey: String,
    @Value("\${ai.model}") private val aiModel: String,
    private val webClientBuilder: WebClient.Builder
) {
    private val webClient = webClientBuilder
        .baseUrl("https://api.deepseek.com")
        .defaultHeader("Content-Type", "application/json")
        .defaultHeader("Authorization", "Bearer $aiApiKey")
        .build()

    private fun systemMessage(username: String): String {
        return  "Ты — внимательный и чуткий личный психолог. " +
                "Твоя задача — помогать человеку лучше понять самого себя" +
                ", его эмоции, переживания, амбиции и внутренние конфликты. " +
                "На основе записей в личном дневнике ты должен проводить глубокий анализ, " +
                "задавать уточняющие вопросы, помогать выявлять скрытые чувства и причины поведения. " +
                "Будь поддерживающим, но честным, стимулируй саморефлексию и способствуй личностному росту. " +
                "Говори мягко, вдумчиво и уважительно. Ты обращаешься по имени $username"
    }

    private fun getChatResponse(chatRequest: ChatRequest) : ChatResponse? {
        return webClient.post()
            .uri("chat/completions")
            .bodyValue(chatRequest)
            .retrieve()
            .onStatus({ it.isError }) { response ->
                response.bodyToMono(String::class.java).map {
                    RuntimeException("AI service error: $it")
                }
            }
            .bodyToMono(ChatResponse::class.java)
            .block()
    }

    // создает название заметки на основе ее содержимого
    fun generateTitle(content: String): String {
        val chatRequest = ChatRequest(
            model = aiModel,
            messages = listOf(AiMessageDto(
                content = "Придумай название для этой заметки длиной не более 30 символов.\n$content",
                role = "user"
            )),
            stream = false
        )

        return getChatResponse(chatRequest)?.choices?.firstOrNull()?.message?.content?.take(30)
            ?: throw IllegalStateException("No AI response from assistant")

    }

    // отвечает пользователю на основе истории сообщений
    fun generateReply(request: AiRequestDto) : String {
        val systemAiMessage = AiMessageDto(
            content = systemMessage(request.username),
            role = "system"
        )
        val chatRequest = ChatRequest(
            model = aiModel,
            messages = listOf(systemAiMessage) + request.messages,
            stream = false
        )

        return getChatResponse(chatRequest)?.choices?.firstOrNull()?.message?.content
            ?: throw IllegalStateException("No AI response from assistant")
    }

}