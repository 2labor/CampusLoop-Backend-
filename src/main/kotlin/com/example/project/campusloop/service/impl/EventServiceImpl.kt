package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.CreateEventCommand
import com.example.project.campusloop.domain.command.UpdateEventCommand
import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.entity.EventLocation
import com.example.project.campusloop.domain.entity.EventParticipant
import com.example.project.campusloop.domain.enum.EventCategory
import com.example.project.campusloop.domain.enum.EventStatus
import com.example.project.campusloop.domain.enum.ParticipantStatus
import com.example.project.campusloop.infra.websocket.realtime.service.EventDispatchService
import com.example.project.campusloop.repository.EventParticipantRepository
import com.example.project.campusloop.repository.EventRepository
import com.example.project.campusloop.service.EventService
import com.example.project.campusloop.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EventServiceImpl (
    private val repository: EventRepository,
    private val userService: UserService,
    private val participantRepository: EventParticipantRepository,
    private val dispatchService: EventDispatchService,
) : EventService {

    @Transactional
    override fun createEvent(
        creatorId: UUID,
        command: CreateEventCommand
    ): Event {
        val creatorUser = userService.getUserById(creatorId) ?:
            throw IllegalArgumentException("Account not found")

        val event = Event(
            title = command.title,
            description = command.description,
            category = command.category,
            eventType = command.eventType,
            eventStatus = EventStatus.UPCOMING,
            location = EventLocation(
                locationName = command.locationName,
                latitude = command.latitude,
                longitude = command.longitude
            ),
            maxParticipants = command.maxParticipants,
            creator = creatorUser,
            startTime = command.startTime
        )

        val savedEvent = repository.save(event)

        val creatorParticipant = EventParticipant(
            event = savedEvent,
            user = creatorUser,
            status = ParticipantStatus.APPROVED
        )
        participantRepository.save(creatorParticipant)

        dispatchService.notifyEventCreated(savedEvent)

        return savedEvent
    }

    @Transactional
    override fun updateEvent(
        operatorId: UUID,
        eventId: UUID,
        command: UpdateEventCommand
    ): Event {
        val event = getEventDetails(eventId)
        validateCreator(operatorId, event)

        if (event.eventStatus != EventStatus.UPCOMING) {
            throw IllegalArgumentException("Cannot update event that is not upcoming")
        }

        command.title?.let { event.title = it }
        command.description?.let { event.description = it }
        command.category?.let { event.category = it }
        command.startTime?.let { event.startTime = it }
        command.maxParticipants?.let { event.maxParticipants = it }

        if (command.locationName != null && command.latitude != null && command.longitude != null) {
            event.location = EventLocation(
                locationName = command.locationName,
                latitude = command.latitude,
                longitude = command.longitude
            )
        }

        val saved = repository.save(event)

        dispatchService.notifyEventUpdated(saved)

        return saved
    }

    @Transactional
    override fun cancelEvent(
        operatorId: UUID,
        eventId: UUID
    ): Event {
        val event = getEventDetails(eventId)
        validateCreator(operatorId, event)

        if (event.eventStatus == EventStatus.COMPLETED) {
            throw IllegalStateException("Cannot cancel an already completed event")
        }

        event.eventStatus = EventStatus.CANCELLED
        val saved = repository.save(event)

        dispatchService.notifyEventUpdated(saved)

        return saved
    }

    @Transactional
    override fun completeEvent(
        operatorId: UUID,
        eventId: UUID
    ): Event {
        val event = getEventDetails(eventId)
        validateCreator(operatorId, event)

        if (event.eventStatus == EventStatus.COMPLETED) {
            throw IllegalStateException("Cannot complete an already completed event")
        }

        event.eventStatus = EventStatus.COMPLETED
        val saved = repository.save(event)

        dispatchService.notifyEventUpdated(saved)

        return saved
    }

    @Transactional(readOnly = true)
    override fun getEventDetails(eventId: UUID): Event {
        return repository.findById(eventId)
            .orElseThrow({ IllegalArgumentException("Event with id $eventId not found") })
    }

    override fun existByEventId(eventId: UUID): Boolean {
        return repository.existsById(eventId)
    }

    @Transactional(readOnly = true)
    override fun getUpcomingEvents(
        category: EventCategory?,
        pageable: Pageable
    ): Slice<Event> {
        return if (category != null) {
            repository.findAllByEventStatusAndCategory(EventStatus.UPCOMING, category, pageable)
        } else {
            repository.findAllByEventStatus(EventStatus.UPCOMING, pageable)
        }
    }

    @Transactional(readOnly = true)
    override fun getMyCreatedEvents(
        userId: UUID,
        pageable: Pageable
    ): Slice<Event> {
        return repository.findAllByCreatorId(userId, pageable)
    }

    private fun validateCreator(userId: UUID, event: Event) {
        if (event.creator.id != userId) {
            throw IllegalArgumentException("Creator is different than user")
        }
    }
}