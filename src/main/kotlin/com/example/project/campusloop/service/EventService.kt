package com.example.project.campusloop.service

import com.example.project.campusloop.domain.command.CreateEventCommand
import com.example.project.campusloop.domain.command.UpdateEventCommand
import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.enum.EventCategory
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface EventService {
    fun createEvent(creatorId: UUID, command: CreateEventCommand): Event
    fun updateEvent(operatorId: UUID, eventId: UUID, command: UpdateEventCommand): Event
    fun cancelEvent(operatorId: UUID, eventId: UUID): Event
    fun completeEvent(operatorId: UUID, eventId: UUID): Event

    fun getEventDetails(eventId: UUID): Event
    fun existByEventId(eventId: UUID): Boolean

    fun getUpcomingEvents(category: EventCategory?, pageable: Pageable): Slice<Event>
    fun getMyCreatedEvents(userId: UUID, pageable: Pageable): Slice<Event>
}