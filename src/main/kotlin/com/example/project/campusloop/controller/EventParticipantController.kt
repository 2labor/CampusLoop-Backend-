package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.ParticipantResponse
import com.example.project.campusloop.domain.enum.ParticipantStatus
import com.example.project.campusloop.domain.mapper.EventParticipantMapper
import com.example.project.campusloop.service.EventParticipantService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/events")
class EventParticipantController (
    private val service: EventParticipantService,
    private val mapper: EventParticipantMapper
) {

    @GetMapping("/{eventId}/participants")
    fun getParticipants(
        @PathVariable eventId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<ParticipantResponse>> {
        val participantsSlice = service.getEventParticipants(eventId, pageable)
        val responseSlice = participantsSlice.map { mapper.toResponse(it) }
        return ResponseEntity.ok(responseSlice)
    }

    @GetMapping("/my-participants")
    fun getMyParticipants(
        @AuthenticationPrincipal userId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<ParticipantResponse>> {
        val attendingSlice = service.getMyAttendingEvents(userId, pageable)
        val responseSlice = attendingSlice.map { mapper.toResponse(it) }
        return ResponseEntity.ok(responseSlice)
    }

    @PostMapping("/{eventId}/participants")
    fun joinEvent(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable eventId: UUID,
    ): ResponseEntity<Void> {
        service.joinToEvent(userId, eventId)
        return ResponseEntity.status(201).build()
    }

    @DeleteMapping("/{eventId}/participants/me")
    fun leaveEvent(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable eventId: UUID,
    ): ResponseEntity<Void> {
        service.leaveEvent(userId, eventId)
        return ResponseEntity.noContent().build()
    }

    @PutMapping("/{eventId}/participants/{targetUserId}")
    fun updateParticipant(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable eventId: UUID,
        @PathVariable targetUserId: UUID,
        @RequestParam status: ParticipantStatus
    ): ResponseEntity<ParticipantResponse> {
        service.updateParticipant(userId, eventId, targetUserId, status)
        return ResponseEntity.ok().build()
    }
}