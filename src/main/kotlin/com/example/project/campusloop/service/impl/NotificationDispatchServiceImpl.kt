package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.dto.NotificationResponse
import com.example.project.campusloop.domain.dto.SystemAnnouncementDto
import com.example.project.campusloop.infra.websocket.RedisWebSocketBroadcaster
import com.example.project.campusloop.service.NotificationDispatchService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class NotificationDispatchServiceImpl (
    private val broadcaster: RedisWebSocketBroadcaster
) : NotificationDispatchService {

    override fun sendToUserDirectly(
        userId: UUID,
        response: NotificationResponse
    ) {
        broadcaster.sendToUser(userId,"/queue/notifications", response)
    }

    override fun broadcastSystem(title: String, body: String) {
        val announcement = SystemAnnouncementDto(
            title = title,
            body = body,
        )

        broadcaster.broadcast("/topic/system/announcements", announcement)
    }
}