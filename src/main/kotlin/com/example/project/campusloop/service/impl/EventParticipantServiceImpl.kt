package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.EventParticipant
import com.example.project.campusloop.domain.enum.EventStatus
import com.example.project.campusloop.domain.enum.EventType
import com.example.project.campusloop.domain.enum.ParticipantStatus
import com.example.project.campusloop.infra.websocket.realtime.service.EventDispatchService
import com.example.project.campusloop.repository.EventParticipantRepository
import com.example.project.campusloop.repository.EventRepository
import com.example.project.campusloop.service.EventParticipantService
import com.example.project.campusloop.service.EventService
import com.example.project.campusloop.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EventParticipantServiceImpl (
    private val repository: EventParticipantRepository,
    private val eventRepository: EventRepository,
    private val eventService: EventService,
    private val userService: UserService,
    private val dispatchService: EventDispatchService
) : EventParticipantService {

    @Transactional(readOnly = true)
    override fun getEventParticipants(
        callerId: UUID,
        eventId: UUID,
        pageable: Pageable
    ): Slice<EventParticipant> {
        val event = eventService.getEventDetails(eventId)

        if (event.eventType == EventType.CLOSED && event.creator.id != callerId) {
            val callerRecord = repository.findByEventIdAndUserId(eventId, callerId)
            if (callerRecord == null || callerRecord.status != ParticipantStatus.APPROVED) {
                throw AccessDeniedException("Only approved participants or the organizer can view participants of a closed event")
            }
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
        val user = userService.getUserById(userId)
            ?: throw IllegalArgumentException("Account with id $userId not found")

        val event = eventRepository.findByIdWithLock(eventId)
            .orElseThrow { IllegalArgumentException("Event with id $eventId not found") }

        if (event.eventStatus != EventStatus.UPCOMING) {
            throw IllegalStateException("Cannot join an event that is not upcoming")
        }

        if (repository.existsByEventIdAndUserId(eventId, userId)) {
            throw IllegalStateException("User is already a participant or has a pending request")
        }

        val initialStatus = if (event.eventType == EventType.OPEN) {
            ParticipantStatus.APPROVED
        } else {
            ParticipantStatus.PENDING
        }

        if (initialStatus == ParticipantStatus.APPROVED && event.maxParticipants != null) {
            val currentApprovedCount = repository.countByEventIdAndStatus(eventId, ParticipantStatus.APPROVED)
            if (currentApprovedCount >= event.maxParticipants!!) {
                throw IllegalStateException("Event has reached its maximum participant limit")
            }
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

        if (participant.event.creator.id == userId) {
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
        val event = eventRepository.findByIdWithLock(eventId)
            .orElseThrow { IllegalArgumentException("Event with id $eventId not found") }

        if (event.creator.id != operatorId) {
            throw AccessDeniedException("You cannot edit an event that is not owned by you")
        }

        val participant = findParticipant(eventId, targetUserId)

        if (newStatus == ParticipantStatus.APPROVED && participant.status != ParticipantStatus.APPROVED) {
            if (event.maxParticipants != null) {
                val currentApprovedCount = repository.countByEventIdAndStatus(eventId, ParticipantStatus.APPROVED)
                if (currentApprovedCount >= event.maxParticipants!!) {
                    throw IllegalStateException("Event has reached its maximum participant limit")
                }
            }
        }

        participant.status = newStatus
        val saved = repository.save(participant)

        dispatchService.notifyParticipantStatusUpdated(eventId, saved)
    }

    private fun findParticipant(eventId: UUID, userId: UUID): EventParticipant {
        return repository.findByEventIdAndUserId(eventId, userId)
            ?: throw IllegalArgumentException("Participant not found for event $eventId")
    }

}
