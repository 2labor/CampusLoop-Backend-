package com.example.project.campusloop.domain.request

import com.example.project.campusloop.domain.enum.EventCategory
import com.example.project.campusloop.domain.enum.EventType
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant

data class CreateEventRequest(
    @field:NotBlank(message = "Event title must not be blank")
    @field:Size(min = 1, max = 120, message = "Event title must be between 1 and 120 characters")
    val title: String,

    @field:Size(max = 2000, message = "Description must not exceed 2000 characters")
    val description: String?,

    val category: EventCategory,
    val eventType: EventType,

    @field:Future(message = "Event start time must be in the future")
    val startTime: Instant,

    @field:NotBlank(message = "Location name must not be blank")
    @field:Size(min = 1, max = 160, message = "Location name must be between 1 and 160 characters")
    val locationName: String,

    @field:DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
    @field:DecimalMax(value = "90.0", message = "Latitude must be <= 90")
    val latitude: Double,

    @field:DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
    @field:DecimalMax(value = "180.0", message = "Longitude must be <= 180")
    val longitude: Double,

    @field:Min(value = 2, message = "Maximum participants must be at least 2")
    @field:Max(value = 500, message = "Maximum participants must not exceed 500")
    val maxParticipants: Int?
)
