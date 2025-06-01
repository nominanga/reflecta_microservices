package com.kfd.noteservice.services

import com.kfd.noteservice.database.entities.Message
import com.kfd.noteservice.database.entities.Note
import com.kfd.noteservice.database.entities.NoteThread
import com.kfd.noteservice.database.repositories.MessageRepository
import com.kfd.noteservice.dto.ai.AiMessageDto
import com.kfd.noteservice.dto.ai.AiRequestDto
import com.kfd.noteservice.enums.MessageSender
import com.kfd.noteservice.services.clients.AiServiceClient
import com.kfd.noteservice.services.clients.UserServiceClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AiMessageService(
    private val messageRepository: MessageRepository,
    private val aiServiceClient: AiServiceClient,
    private val userServiceClient: UserServiceClient,
) {
    private fun Message.toAiMessageDto(): AiMessageDto =
        AiMessageDto(
            content = this.text,
            role =
                when (this.sender) {
                    MessageSender.AI -> "assistant"
                    MessageSender.USER -> "user"
                },
        )

    fun createAiMessage(
        userId: Long,
        note: Note,
        previousMessages: List<Message>,
    ): Message {
        val username = userServiceClient.getUserName(userId)
        val noteAiMessage =
            AiMessageDto(
                content = note.body,
                role = "user",
            )
        val aiRequestDto =
            AiRequestDto(
                username = username,
                messages = listOf(noteAiMessage) + previousMessages.map { it.toAiMessageDto() },
            )

        val aiResponse = aiServiceClient.getAiResponse(aiRequestDto)
        return createMessage(note.noteThread!!, aiResponse, MessageSender.AI)
    }

    fun generateTitle(body: String): String {
        return aiServiceClient.generateTitle(body)
    }

    @Transactional
    fun createUserMessage(
        note: Note,
        text: String?,
    ): Message {
        require(!text.isNullOrBlank()) { "Message can not be empty" }
        createMessage(note.noteThread!!, text, MessageSender.USER)

        val messages = getMessages(note.noteThread!!)

        return createAiMessage(note.userId, note, messages)
    }

    fun createMessage(
        noteThread: NoteThread,
        text: String,
        sender: MessageSender,
    ): Message {
        return messageRepository.save(
            Message(
                noteThread = noteThread,
                text = text,
                sender = sender,
            ),
        )
    }

    fun getMessages(noteThread: NoteThread): List<Message> {
        return messageRepository.findAllByNoteThreadOrderByCreatedAtAsc(noteThread)
    }

    fun deleteMessages(noteThread: NoteThread) {
        messageRepository.deleteAllByNoteThread(noteThread)
    }
}
