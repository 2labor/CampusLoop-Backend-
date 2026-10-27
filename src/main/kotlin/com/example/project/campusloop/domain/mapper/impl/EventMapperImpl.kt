package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.command.CreateEventCommand
import com.example.project.campusloop.domain.command.UpdateEventCommand
import com.example.project.campusloop.domain.dto.EventResponse
import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.mapper.EventMapper
import com.example.project.campusloop.domain.request.CreateEventRequest
import com.example.project.campusloop.domain.request.UpdateEventRequest
import org.springframework.stereotype.Component

@Component
class EventMapperImpl : EventMapper {
    override fun toCommand(request: CreateEventRequest): CreateEventCommand {
        return CreateEventCommand(
            title = request.title,
            description = request.description,
            category = request.category,
            eventType = request.eventType,
            startTime = request.startTime,
            locationName = request.locationName,
            latitude = request.latitude,
            longitude = request.longitude,
            maxParticipants = request.maxParticipants
        )
    }

    override fun toCommand(request: UpdateEventRequest): UpdateEventCommand {
        return UpdateEventCommand(
            title = request.title,
            description = request.description,
            category = request.category,
            startTime = request.startTime,
            locationName = request.locationName,
            latitude = request.latitude,
            longitude = request.longitude,
            maxParticipants = request.maxParticipants
        )
    }

    override fun toResponse(event: Event): EventResponse {
        return EventResponse(
            id = event.id ?: throw IllegalStateException("Event ID cannot be null"),
            title = event.title,
            description = event.description,
            category = event.category,
            eventType = event.eventType,
            eventStatus = event.eventStatus,
            startTime = event.startTime,
            locationName = event.location.locationName,
            latitude = event.location.latitude,
            longitude = event.location.longitude,
            maxParticipants = event.maxParticipants,
            creatorId = event.creator.id ?: throw IllegalStateException("Creator ID cannot be null"),
            createdAt = event.createdAt,
            updatedAt = event.updatedAt
        )
    }
}