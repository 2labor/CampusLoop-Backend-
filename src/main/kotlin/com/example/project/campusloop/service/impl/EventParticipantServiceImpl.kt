package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.entity.EventParticipant
import com.example.project.campusloop.domain.enum.EventStatus
import com.example.project.campusloop.domain.enum.EventType
import com.example.project.campusloop.domain.enum.ParticipantStatus
import com.example.project.campusloop.infra.websocket.realtime.service.EventDispatchService
import com.example.project.campusloop.repository.EventParticipantRepository
import com.example.project.campusloop.service.AccountService
import com.example.project.campusloop.service.EventParticipantService
import com.example.project.campusloop.service.EventService
import com.example.project.campusloop.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.UUID

@Service
class EventParticipantServiceImpl (
    private val repository: EventParticipantRepository,
    private val eventService: EventService,
    private val userService: UserService,
    private val dispatchService: EventDispatchService
) : EventParticipantService {

    @Transactional(readOnly = true)
    override fun getEventParticipants(
        eventId: UUID,
        pageable: Pageable
    ): Slice<EventParticipant> {
        if (!eventService.existByEventId(eventId)) {
            throw IllegalArgumentException("Event with id $eventId not found")
        }

        return repository.findAllByEventId(eventId, pageable)
    }

    @Transactional(readOnly = true)
    override fun getMyAttendingEvents(
        userId: UUID,
        pageable: Pageable
    ): Slice<EventParticipant> {
        return repository.findAllByUserId(userId, pageable)
    }

    @Transactional
    override fun joinToEvent(userId: UUID, eventId: UUID) {
        val user = userService.getUserById(userId) ?:
            throw IllegalArgumentException("Account with id $userId not found")

        val event = eventService.getEventDetails(eventId)

        if (event.eventStatus != EventStatus.UPCOMING || event.startTime.isBefore(Instant.now())) {
            throw IllegalStateException("Cannot join an event that is not upcoming")
        }

        if (repository.existsByEventIdAndUserId(eventId, userId)) {
            throw IllegalArgumentException("User is already a participant or has a pending request")
        }

        if (event.maxParticipants != null) {
            val currentApprovedCount = repository.countByEventIdAndStatus(eventId, ParticipantStatus.APPROVED)
            if (currentApprovedCount >= event.maxParticipants!!) {
                throw IllegalStateException("Event has reached its maximum participant limit")
            }
        }

        val initialStatus = if (event.eventType == EventType.OPEN) {
            ParticipantStatus.APPROVED
        } else {
            ParticipantStatus.PENDING
        }


        val participant = EventParticipant(
            event = event,
            user = user,
            status = initialStatus
        )

        val saved = repository.save(participant)

        dispatchService.notifyParticipantJoined(eventId, saved)
    }

    @Transactional
    override fun leaveEvent(userId: UUID, eventId: UUID) {
        val participant = findParticipant(eventId, userId)

        val event = participant.event

        if (event.startTime.isBefore(Instant.now()) || event.eventStatus == EventStatus.CANCELLED || event.eventStatus == EventStatus.COMPLETED) {
            throw IllegalStateException("Status of event with id $eventId is different from the upcoming")
        }

        if (event.creator.id == userId) {
            throw IllegalStateException("Event creator cannot leave their own event")
        }

        repository.delete(participant)

        dispatchService.notifyParticipantLeft(eventId, userId)
    }

    @Transactional
    override fun updateParticipant(
        operatorId: UUID,
        eventId: UUID,
        targetUserId: UUID,
        newStatus: ParticipantStatus
    ) {
        val event = eventService.getEventDetails(eventId)

        if (event.creator.id != operatorId) {
            throw IllegalArgumentException("You cannot edit event that is not owed by you")
        }

        if (event.creator.id == targetUserId) {
            throw IllegalArgumentException("You cannot edit participant due to the ownership")
        }

        if (event.eventStatus == EventStatus.CANCELLED || event.eventStatus == EventStatus.COMPLETED || event.startTime.isBefore(Instant.now())) {
            throw IllegalStateException("You cannot edit participant for event that is currently running or its status is different to upcoming")
        }

        val participant = findParticipant(eventId, targetUserId)

        if (newStatus == ParticipantStatus.APPROVED && participant.status != ParticipantStatus.APPROVED) {
            if (event.maxParticipants != null) {
                val currentApprovedCount = repository.countByEventIdAndStatus(eventId, ParticipantStatus.APPROVED)
                if (currentApprovedCount >= event.maxParticipants!!) {
                    throw IllegalStateException("Event has reached its maximum participant limit")
                }
            }

            if (event.startTime.isBefore(Instant.now())) {
                throw IllegalStateException("Event has reached its start time")
            }
        }

        participant.status = newStatus
        val saved = repository.save(participant)

        dispatchService.notifyParticipantStatusUpdated(eventId, saved)
    }

    @Transactional
    override fun addToEventParticipant(
        userId: UUID,
        eventId: UUID
    ): EventParticipant {
        val user = userService.getUserById(userId)
            ?: throw IllegalArgumentException("User with id $userId not found")
        val event = eventService.getEventDetails(eventId)

        if (repository.existsByEventIdAndUserId(eventId, userId)) {
            throw IllegalArgumentException("User is already a participant or has a pending request")
        }

        if (
            event.eventStatus != EventStatus.UPCOMING
            || event.startTime.isBefore(Instant.now())
            || event.maxParticipants != null && event.maxParticipants!! <= repository.countByEventIdAndStatus(eventId, ParticipantStatus.APPROVED)
            ) {
            throw IllegalArgumentException("Event has reached its maximum participant limit or its not available anymore")
        }

        val participant = EventParticipant(
            event = event,
            user = user,
            status = ParticipantStatus.APPROVED
        )

        return repository.save(participant)
    }

    private fun findParticipant(eventId: UUID, userId: UUID): EventParticipant {
        return repository.findByEventIdAndUserId(eventId, userId)
            ?: throw IllegalArgumentException("Event with id $eventId not found")
    }

}