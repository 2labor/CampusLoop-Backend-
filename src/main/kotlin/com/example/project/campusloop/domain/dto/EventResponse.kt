package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.EventCategory
import com.example.project.campusloop.domain.enum.EventStatus
import com.example.project.campusloop.domain.enum.EventType
import java.time.LocalDateTime
import java.util.UUID

data class EventResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val category: EventCategory,
    val eventType: EventType,
    val eventStatus: EventStatus,
    val startTime: LocalDateTime,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val maxParticipants: Int?,
    val creatorId: UUID,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
