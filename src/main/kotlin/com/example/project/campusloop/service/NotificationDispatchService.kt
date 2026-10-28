package com.example.project.campusloop.service

import com.example.project.campusloop.domain.dto.NotificationResponse
import java.util.UUID

interface NotificationDispatchService {
    fun sendToUserDirectly(userId: UUID, response: NotificationResponse)
    fun broadcastSystem(title: String, body: String)
}