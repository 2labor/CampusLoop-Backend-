package com.example.project.campusloop.domain.command

import com.example.project.campusloop.domain.enum.EventCategory
import java.time.Instant

data class UpdateEventCommand(
    val title: String?,
    val description: String?,
    val category: EventCategory?,
    val startTime: Instant?,
    val locationName: String?,
    val latitude: Double?,
    val longitude: Double?,
    val maxParticipants: Int?
)
