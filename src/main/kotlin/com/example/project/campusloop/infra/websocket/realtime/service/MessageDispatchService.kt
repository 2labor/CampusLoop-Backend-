package com.example.project.campusloop.infra.websocket.realtime.service

import com.example.project.campusloop.domain.dto.MessageResponse
import com.example.project.campusloop.domain.entity.Message
import com.example.project.campusloop.domain.mapper.MessageMapper
import com.example.project.campusloop.infra.websocket.realtime.RealtimePublisher
import com.example.project.campusloop.infra.websocket.realtime.enum.RealtimeAction
import org.springframework.stereotype.Service

@Service
class MessageDispatchService (
    private val realtimePublisher: RealtimePublisher,
    private val mapper: MessageMapper
) {
    fun notifyMessageCreated(channelId: Long, message: Message) {
        val response = mapper.toResponse(message)
        realtimePublisher.publish(
            destination = "/topic/channels/$channelId/messages",
            action = RealtimeAction.CREATED,
            entityId = response .id,
            payload = response
        )
    }

    fun notifyMessageUpdated(channelId: Long, message: Message) {
        val response = mapper.toResponse(message)
        realtimePublisher.publish(
            destination = "/topic/channels/$channelId/messages",
            action = RealtimeAction.UPDATED,
            entityId = response.id,
            payload = response
        )
    }

    fun notifyMessageDeleted(channelId: Long, messageId: Long) {
        realtimePublisher.publish(
            destination = "/topic/channels/$channelId/messages",
            action = RealtimeAction.DELETED,
            entityId = messageId,
            payload = null
        )
    }
}