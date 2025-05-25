package com.kfd.aiservice.services

import com.kfd.aiservice.chat.ChatRequest
import com.kfd.aiservice.chat.ChatResponse
import com.kfd.aiservice.dto.AiMessageDto
import com.kfd.aiservice.dto.AiRequestDto
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class AiService(
    @Value("\${ai.api-key}") private val aiApiKey: String,
    @Value("\${ai.model}") private val aiModel: String,
    private val webClientBuilder: WebClient.Builder
) {

    private val logger = LoggerFactory.getLogger(AiService::class.java)

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
        return try {
            webClient.post()
                .uri("chat/completions")
                .bodyValue(chatRequest)
                .retrieve()
                .onStatus({ it.isError }) { response ->
                    response.bodyToMono(String::class.java).flatMap {
                        logger.warn("AI service returned error: {}", it)
                        Mono.empty()
                    }
                }
                .bodyToMono(ChatResponse::class.java)
                .timeout(Duration.ofSeconds(5))
                .onErrorResume {
                    logger.warn("DeepSeek API error: {}", it.message)
                    Mono.empty()
                }
                .block()
        } catch (e: Exception) {
            logger.warn("DeepSeek API call failed: {}", e.message)
            null
        }
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

        return getChatResponse(chatRequest)
            ?.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?.take(255)
            ?.trim()
            ?: "Untitled"

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

        return getChatResponse(chatRequest)
            ?.choices
            ?.firstOrNull()
            ?.message
            ?.content
            ?.trim()
            ?: "Извините, сейчас я не могу ответить. Попробуйте позже."
    }

}