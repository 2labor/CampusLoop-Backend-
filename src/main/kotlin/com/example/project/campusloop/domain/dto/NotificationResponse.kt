package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.entity.Notification
import com.example.project.campusloop.domain.enum.NotificationType
import java.time.Instant
import java.util.UUID

data class NotificationResponse(
    val id: UUID,
    val title: String,
    val message: String,
    val type: NotificationType,
    val isRead: Boolean,
    val createdAt: Instant,
    val updateAt: Instant
)

fun Notification.toResponse() = NotificationResponse(
    id = id!!,
    title = title,
    message = message,
    type = type,
    isRead = isRead,
    createdAt = createdAt,
    updateAt = updatedAt
)