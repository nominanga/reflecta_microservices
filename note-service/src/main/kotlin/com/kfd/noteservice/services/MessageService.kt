package com.kfd.noteservice.services

import com.kfd.noteservice.database.entities.Message
import com.kfd.noteservice.database.entities.NoteThread
import com.kfd.noteservice.database.repositories.MessageRepository
import com.kfd.noteservice.enums.MessageSender
import org.springframework.stereotype.Service

@Service
class MessageService(
    private val messageRepository: MessageRepository,
) {
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
