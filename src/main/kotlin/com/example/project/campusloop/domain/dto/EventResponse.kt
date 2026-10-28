package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.EventCategory
import com.example.project.campusloop.domain.enum.EventStatus
import com.example.project.campusloop.domain.enum.EventType
import java.time.Instant
import java.util.UUID

data class EventResponse(
    val id: UUID,
    val title: String,
    val description: String?,
    val category: EventCategory,
    val eventType: EventType,
    val eventStatus: EventStatus,
    val startTime: Instant,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val maxParticipants: Int?,
    val creatorId: UUID,
    val createdAt: Instant,
    val updatedAt: Instant
)
