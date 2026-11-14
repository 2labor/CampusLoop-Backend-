package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.dto.DirectChannelResponse
import com.example.project.campusloop.domain.dto.DirectChannelSummary
import com.example.project.campusloop.domain.mapper.ChannelMapper
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ChannelMapperImpl : ChannelMapper {
    override fun toResponse(entity: DirectChannelSummary, currentUserId: UUID): DirectChannelResponse {
        val channel = entity.channel
        val counterpart = entity.counterpart

        return DirectChannelResponse(
            id = channel.id!!,
            userId = counterpart.id!!,
            name = counterpart.username,
            lastMessage = channel.lastMessageContent,
            isMine = channel.lastSenderId == currentUserId,
            lastActivityAt = channel.lastActivityAt,
            unreadCount = entity.unreadCount
        )
    }
}