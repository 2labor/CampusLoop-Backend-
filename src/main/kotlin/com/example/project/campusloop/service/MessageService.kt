package com.example.project.campusloop.service

import com.example.project.campusloop.domain.command.CreateMessageCommand
import com.example.project.campusloop.domain.command.EditMessageCommand
import com.example.project.campusloop.domain.entity.Message
import com.example.project.campusloop.domain.enum.HistoryDirection
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface MessageService {
    fun sendMessage(authorId: UUID, channelId: Long, command: CreateMessageCommand): Message
    fun editMessage(operatorId: UUID, messageId:Long, command: EditMessageCommand): Message
    fun deleteMessage(operatorId: UUID, messageId:Long)
    fun getMessageById(operatorId: UUID, messageId: Long): Message
    fun getLastMessage(operatorId: UUID, channelId: Long): Message?
    fun getChannelHistory(operatorId: UUID, channelId: Long, cursorId: Long?, direction: HistoryDirection = HistoryDirection.BEFORE, pageable: Pageable): Slice<Message>
}