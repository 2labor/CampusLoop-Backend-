package com.example.project.campusloop.domain.request

import com.example.project.campusloop.domain.enum.EventCategory
import com.example.project.campusloop.domain.enum.EventType
import java.time.Instant

data class CreateEventRequest(
    val title: String,
    val description: String?,
    val category: EventCategory,
    val eventType: EventType,
    val startTime: Instant,
    val locationName: String,
    val latitude: Double,
    val longitude: Double,
    val maxParticipants: Int?
)
