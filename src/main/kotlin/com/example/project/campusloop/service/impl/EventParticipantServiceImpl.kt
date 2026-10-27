package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.entity.EventParticipant
import com.example.project.campusloop.domain.enum.EventStatus
import com.example.project.campusloop.domain.enum.EventType
import com.example.project.campusloop.domain.enum.ParticipantStatus
import com.example.project.campusloop.repository.EventParticipantRepository
import com.example.project.campusloop.service.AccountService
import com.example.project.campusloop.service.EventParticipantService
import com.example.project.campusloop.service.EventService
import com.example.project.campusloop.service.UserService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class EventParticipantServiceImpl (
    private val repository: EventParticipantRepository,
    private val eventService: EventService,
    private val userService: UserService
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
        accountId: UUID,
        pageable: Pageable
    ): Slice<EventParticipant> {
        return repository.findAllByUserId(accountId, pageable)
    }

    @Transactional
    override fun joinToEvent(userId: UUID, eventId: UUID) {
        val user = userService.getUserById(userId) ?:
            throw IllegalArgumentException("Account with id $userId not found")

        val event = eventService.getEventDetails(eventId)

        if (event.eventStatus != EventStatus.UPCOMING) {
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

        repository.save(participant)
    }

    @Transactional
    override fun leaveEvent(userId: UUID, eventId: UUID) {
        val participant = findParticipant(eventId, userId)

        if (participant.event.creator.id == userId) {
            throw IllegalStateException("Event creator cannot leave their own event")
        }

        repository.delete(participant)
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
        repository.save(participant)
    }

    private fun findParticipant(eventId: UUID, userId: UUID): EventParticipant {
        return repository.findByEventIdAndUserId(eventId, userId)
            ?: throw IllegalArgumentException("Event with id $eventId not found")
    }

}