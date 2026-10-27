package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.EventResponse
import com.example.project.campusloop.domain.enum.EventCategory
import com.example.project.campusloop.domain.mapper.EventMapper
import com.example.project.campusloop.domain.request.CreateEventRequest
import com.example.project.campusloop.domain.request.UpdateEventRequest
import com.example.project.campusloop.service.EventService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/events")
class EventController(
    private val service: EventService,
    private val mapper: EventMapper
) {

    @PostMapping
    fun createEvent(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody @Valid request: CreateEventRequest
    ): ResponseEntity<EventResponse> {
        val command = mapper.toCommand(request)
        val event = service.createEvent(userId, command)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(event))
    }


    @PutMapping("/{eventId}")
    fun updateEvent(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody @Valid request: UpdateEventRequest,
        @PathVariable eventId: UUID
    ): ResponseEntity<EventResponse> {
        val command = mapper.toCommand(request)
        val event = service.updateEvent(userId, eventId, command)
        return ResponseEntity.ok(mapper.toResponse(event))
    }

    @DeleteMapping("/{eventId}/cancel")
    fun cancelEvent(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable eventId: UUID
    ): ResponseEntity<EventResponse> {
        val event = service.cancelEvent(userId, eventId)
        return ResponseEntity.ok(mapper.toResponse(event))
    }

    @PutMapping("/{eventId}/complete")
    fun completeEvent(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable eventId: UUID
    ): ResponseEntity<EventResponse> {
        val event = service.completeEvent(userId, eventId)
        return ResponseEntity.ok(mapper.toResponse(event))
    }

    @GetMapping("/{eventId}")
    fun getEventDetails(
        @PathVariable eventId: UUID
    ): ResponseEntity<EventResponse> {
        val event = service.getEventDetails(eventId)
        return ResponseEntity.ok(mapper.toResponse(event))
    }

    @GetMapping("/upcoming")
    fun getUpcomingEvents(
        @RequestParam(required = false) category: EventCategory?,
        pageable: Pageable
    ): ResponseEntity<Slice<EventResponse>> {
        val eventsSlice = service.getUpcomingEvents(category, pageable)
        val responseSlice = eventsSlice.map { mapper.toResponse(it) }
        return ResponseEntity.ok(responseSlice)
    }

    @GetMapping("/my/created")
    fun getMyCreatedEvents(
        @AuthenticationPrincipal userId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<EventResponse>> {
        val eventsSlice = service.getMyCreatedEvents(userId, pageable)
        val responseSlice = eventsSlice.map { mapper.toResponse(it) }
        return ResponseEntity.ok(responseSlice)
    }
}