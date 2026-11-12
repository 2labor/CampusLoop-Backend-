package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.command.CreateMessageCommand
import com.example.project.campusloop.domain.command.EditMessageCommand
import com.example.project.campusloop.domain.dto.LastMessageResponse
import com.example.project.campusloop.domain.dto.MessageResponse
import com.example.project.campusloop.domain.entity.Message
import com.example.project.campusloop.domain.mapper.AttachmentMapper
import com.example.project.campusloop.domain.mapper.MessageMapper
import com.example.project.campusloop.domain.request.CreateMessageRequest
import com.example.project.campusloop.domain.request.EditMessageRequest
import org.springframework.stereotype.Component

@Component
class MessageMapperImpl(
    private val attachmentMapper: AttachmentMapper
) : MessageMapper {
    override fun toResponse(entity: Message): MessageResponse {
        return MessageResponse(
            id = entity.id!!,
            content = entity.content,
            senderName = entity.sender.username,
            channelId = entity.channel.id!!,
            parentContent = entity.parentMessage?.content,
            attachments = attachmentMapper.toResponseList(entity.attachments),
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
        )
    }

    override fun toLastMessageResponse(entity: Message): LastMessageResponse {
        return LastMessageResponse(
            id = entity.id!!,
            content = entity.content,
            senderName = entity.sender.username,
            createdAt = entity.createdAt,
        )
    }

    override fun toCommand(request: CreateMessageRequest): CreateMessageCommand {
        return CreateMessageCommand(
            content = request.content,
            parentMessageId = request.parentMessageId,
            attachments = request.attachments,
        )
    }

    override fun toCommand(request: EditMessageRequest): EditMessageCommand {
        return EditMessageCommand(
            content = request.content
        )
    }
}