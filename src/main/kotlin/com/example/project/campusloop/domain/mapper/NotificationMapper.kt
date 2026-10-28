package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.dto.NotificationResponse
import com.example.project.campusloop.domain.entity.Notification

interface NotificationMapper {
    fun toResponse(entity: Notification): NotificationResponse
}