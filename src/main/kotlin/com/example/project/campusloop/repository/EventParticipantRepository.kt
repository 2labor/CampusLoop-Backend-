package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.EventParticipant
import com.example.project.campusloop.domain.enum.EventType
import com.example.project.campusloop.domain.enum.ParticipantStatus
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.domain.Pageable
import java.util.UUID

interface EventParticipantRepository : JpaRepository<EventParticipant, UUID> {
    fun countByEventIdAndStatus(eventId: UUID, status: ParticipantStatus): Long
    fun existsByEventIdAndUserId(eventId: UUID, userId: UUID): Boolean
    fun findByEventIdAndUserId(eventId: UUID, userId: UUID): EventParticipant?

    fun findAllByEventId(eventId: UUID, pageable: Pageable): Slice<EventParticipant>
    fun findAllByUserId(userId: UUID, pageable: Pageable): Slice<EventParticipant>
}