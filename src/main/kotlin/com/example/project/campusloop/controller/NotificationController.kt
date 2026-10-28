package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.NotificationResponse
import com.example.project.campusloop.domain.dto.NotificationUnreadCounterResponse
import com.example.project.campusloop.domain.dto.toResponse
import com.example.project.campusloop.service.NotificationService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("api/v1/notifications")
class NotificationController (
    private val service: NotificationService
) {

    @GetMapping()
    fun getMyNotifications(
        @AuthenticationPrincipal userId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<NotificationResponse>> {
        val notificationSlice = service.getNotifications(userId, pageable)
            .map { it.toResponse() }
        return ResponseEntity.ok(notificationSlice)
    }

    @GetMapping("/count-unread")
    fun getUnreadCount(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<NotificationUnreadCounterResponse> {
        val counter = service.getUnreadCount(userId)
        return ResponseEntity.ok(NotificationUnreadCounterResponse(counter))
    }

    @PatchMapping("/{notificationId}/read")
    fun markAsRead(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable notificationId: UUID,
    ): ResponseEntity<Void> {
        service.markAsRead(notificationId, userId)
        return ResponseEntity.noContent().build()
    }

    @PatchMapping("/raed-all")
    fun raedAll(
        @AuthenticationPrincipal userId: UUID,
    ): ResponseEntity<Void> {
        service.markAllAsRead(userId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{notificationId}")
    fun delete(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable notificationId: UUID
    ): ResponseEntity<Void> {
        service.deleteMyNotification(userId, notificationId)
        return ResponseEntity.noContent().build()
    }
}