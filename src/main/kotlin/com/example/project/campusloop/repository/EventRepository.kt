package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.enum.EventCategory
import com.example.project.campusloop.domain.enum.EventStatus
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional
import java.util.UUID

interface EventRepository : JpaRepository<Event, UUID> {
    fun findAllByEventStatus(status: EventStatus, pageable: Pageable): Slice<Event>
    fun findAllByEventStatusAndCategory(status: EventStatus, category: EventCategory, pageable: Pageable): Slice<Event>
    fun findAllByCreatorId(creatorId: UUID, pageable: Pageable): Slice<Event>

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Event e WHERE e.id = :eventId")
    fun findByIdWithLock(@Param("eventId") eventId: UUID): Optional<Event>
}
