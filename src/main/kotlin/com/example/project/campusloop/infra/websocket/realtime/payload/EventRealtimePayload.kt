package com.example.project.campusloop.infra.websocket.realtime.payload

import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.enum.EventCategory
import com.example.project.campusloop.domain.enum.EventStatus
import com.example.project.campusloop.domain.enum.EventType
import java.time.Instant
import java.util.UUID

data class EventRealtimePayload(
    val id: UUID,
    val title: String,
    val description: String?,
    val category: EventCategory,
    val eventType: EventType,
    val eventStatus: EventStatus,
    val location: EventLocationPayload,
    val maxParticipant: Int?,
    val startTime: Instant,
    val creatorId: UUID
)

fun Event.toPayload() = EventRealtimePayload(
    id = id!!,
    title = title,
    description = description,
    category = category,
    eventType = eventType,
    eventStatus = eventStatus,
    location = location.let {
        EventLocationPayload(
            locationName = it.locationName,
            latitude = it.latitude,
            longitude = it.longitude
        )
    },
    maxParticipant = maxParticipants,
    startTime = startTime,
    creatorId = creator.id!!
)