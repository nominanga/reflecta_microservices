package com.kfd.aiservice.services

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kfd.aiservice.chat.ChatRequest
import com.kfd.aiservice.chat.ChatResponse
import com.kfd.aiservice.dto.AiMessageDto
import com.kfd.aiservice.dto.AiRequestDto
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient
import java.time.Duration
import java.util.concurrent.TimeUnit

@Service
class AiService(
    @Value("\${ai.api-key}") private val aiApiKey: String,
    @Value("\${ai.model}") private val aiModel: String,
    private val webClientBuilder: WebClient.Builder,
) {
    @PostConstruct
    fun logConfig() {
        logger.info("Using AI model: $aiModel")
        logger.info("API key is present: ${aiApiKey.isNotBlank()}")
    }

    private val logger = LoggerFactory.getLogger(AiService::class.java)
    private val objectMapper = jacksonObjectMapper()

    private val httpClient =
        HttpClient.create()
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
            .doOnConnected { conn ->
                conn.addHandlerLast(ReadTimeoutHandler(60, TimeUnit.SECONDS))
            }
    private val webClient =
        webClientBuilder
            .baseUrl("https://api.deepseek.com")
            .clientConnector(ReactorClientHttpConnector(httpClient))
            .defaultHeader("Content-Type", "application/json")
            .defaultHeader("Authorization", "Bearer $aiApiKey")
            .build()

    private fun systemMessage(username: String): String {
        return """
            Ты — профессиональный психолог, обладающий глубокими знаниями в области когнитивно-поведенческой терапии, гештальт-подхода и психоанализа. 
            Твоя задача — анализировать личные записи пользователя, выявлять эмоциональные паттерны, когнитивные искажения, внутренние конфликты, а также давать чёткую и обоснованную обратную связь.
            
            Обращай внимание на формулировки, выбирай точные термины (например: деперсонализация, тревожность, эмоциональное выгорание, защитные механизмы, рационализация, прокрастинация).
            
            Используй метафоры, аналогии, конкретные примеры и терапевтические вопросы. Стимулируй глубокую саморефлексию. Показывай, как определённые мысли и убеждения влияют на чувства и поведение. 
            
            Не ограничивайся поддержкой — давай практические рекомендации, техники (например: дыхательные упражнения, ведение журнала мыслей, метод Сократа, "письмо внутреннему ребёнку").
            
            Будь внимательным и тактичным, но не уходи от сути — прямо обозначай внутренние противоречия, возможные ошибки мышления и избегания.
            
            Имя пользователя: "$username". Используй это имя точно в таком виде — **без перевода, склонения или транслитерации**. Только "$username".
            """.trimIndent()
    }

    private fun getChatResponse(chatRequest: ChatRequest): ChatResponse? {
        logger.info("Sending ChatRequest: {}", objectMapper.writeValueAsString(chatRequest))

        val mono =
            webClient.post()
                .uri("chat/completions")
                .bodyValue(chatRequest)
                .retrieve()
                .onStatus({ it.isError }) { response ->
                    response.bodyToMono(String::class.java).flatMap {
                        logger.warn("AI service returned error body: {}", it)
                        Mono.empty()
                    }
                }
                .bodyToMono(ChatResponse::class.java)
                .timeout(Duration.ofSeconds(60))
                .onErrorResume {
                    logger.warn("DeepSeek API error: {}", it.message)
                    Mono.empty()
                }

        logger.info("About to block and wait for DeepSeek response")
        val response = mono.block()
        logger.info("Received ChatResponse: {}", response)
        return response
    }

    // создает название заметки на основе ее содержимого
    fun generateTitle(content: String): String {
        val chatRequest =
            ChatRequest(
                model = aiModel,
                messages =
                    listOf(
                        AiMessageDto(
                            content =
                                "Скажи ровно одно самое подходящее название для этой заметки, " +
                                    "длиной не больше 30 символов. Не надо примеров, твой ответ должен содержать только " +
                                    "самое подходящее название.\nЗаметка:\n$content",
                            role = "user",
                        ),
                    ),
                stream = false,
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
    fun generateReply(request: AiRequestDto): String {
        val systemAiMessage =
            AiMessageDto(
                content = systemMessage(request.username),
                role = "system",
            )
        val chatRequest =
            ChatRequest(
                model = aiModel,
                messages = listOf(systemAiMessage) + request.messages,
                stream = false,
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
