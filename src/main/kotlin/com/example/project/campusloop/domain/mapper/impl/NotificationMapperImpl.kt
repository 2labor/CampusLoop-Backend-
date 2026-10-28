package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.dto.NotificationResponse
import com.example.project.campusloop.domain.entity.Notification
import com.example.project.campusloop.domain.mapper.NotificationMapper
import org.springframework.stereotype.Component

@Component
class NotificationMapperImpl : NotificationMapper {
    override fun toResponse(entity: Notification): NotificationResponse {
        return NotificationResponse(
            id = entity.id!!,
            title = entity.title,
            message = entity.message,
            type = entity.type,
            isRead = entity.isRead,
            createdAt = entity.createdAt,
            updateAt = entity.updatedAt
        )
    }
}