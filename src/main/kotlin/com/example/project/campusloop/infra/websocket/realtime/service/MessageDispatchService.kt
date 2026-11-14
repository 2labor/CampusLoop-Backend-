package com.example.project.campusloop.infra.websocket.realtime.service

import com.example.project.campusloop.domain.dto.DirectMessageNotification
import com.example.project.campusloop.domain.dto.MessageResponse
import com.example.project.campusloop.domain.entity.Message
import com.example.project.campusloop.domain.mapper.MessageMapper
import com.example.project.campusloop.infra.websocket.realtime.RealtimePublisher
import com.example.project.campusloop.infra.websocket.realtime.enum.RealtimeAction
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MessageDispatchService(
    private val realtimePublisher: RealtimePublisher,
    private val mapper: MessageMapper
) {
    fun notifyMessageCreated(
        channelId: Long,
        message: Message,
        recipientUserIds: Collection<UUID> = emptyList()
    ) {
        val response = mapper.toResponse(message)
        realtimePublisher.publish(
            destination = "/topic/channels/$channelId/messages",
            action = RealtimeAction.CREATED,
            entityId = response .id,
            payload = response
        )

        val messagePreview = response.content
            .trim()
            .takeIf { it.isNotBlank() }
            ?.take(100)
            ?: "Sent an attachment"
        val notification = DirectMessageNotification(
            channelId = channelId,
            senderName = response.senderName,
            message = messagePreview
        )
        recipientUserIds.forEach { recipientId ->
            realtimePublisher.sendToUser(
                userId = recipientId,
                destination = "/queue/messages",
                action = RealtimeAction.CREATED,
                entityId = response.id,
                payload = notification
            )
        }
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