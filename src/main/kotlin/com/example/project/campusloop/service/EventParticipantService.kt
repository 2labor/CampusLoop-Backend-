package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.entity.EventParticipant
import com.example.project.campusloop.domain.enum.ParticipantStatus
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface EventParticipantService {
    fun getEventParticipants(eventId: UUID, pageable: Pageable): Slice<EventParticipant>
    fun getMyAttendingEvents(userId: UUID, pageable: Pageable): Slice<EventParticipant>

    fun joinToEvent(userId: UUID, eventId: UUID)
    fun leaveEvent(userId: UUID, eventId: UUID)

    fun updateParticipant(
        operatorId: UUID,
        eventId: UUID,
        targetUserId: UUID,
        newStatus: ParticipantStatus)
}