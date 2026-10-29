package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.FriendCountResponse
import com.example.project.campusloop.domain.dto.FriendshipResponse
import com.example.project.campusloop.domain.mapper.FriendshipMapper
import com.example.project.campusloop.service.FriendshipService
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
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/friends")
class FriendshipController (
    private val service: FriendshipService,
    private val mapper: FriendshipMapper
){
    @PostMapping("/requests/{targetUserId}")
    fun sendFriendRequest(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable targetUserId: UUID
    ): ResponseEntity<FriendshipResponse> {
        val friendship = service.sendFriendRequest(userId, targetUserId)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(userId,friendship))
    }

    @PutMapping("/requests/{requesterId}/accept")
    fun acceptFriendRequest(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable requesterId: UUID
    ): ResponseEntity<Void> {
        service.acceptFriendRequest(userId, requesterId)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/requests/{requesterId}/reject")
    fun rejectFriendRequest(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable requesterId: UUID
    ): ResponseEntity<Void> {
        service.rejectFriendRequest(userId, requesterId)
        return ResponseEntity.noContent().build()
    }

    @DeleteMapping("/{targetUserId}")
    fun removeFriendOrCancelRequest(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable targetUserId: UUID
    ): ResponseEntity<Void> {
        service.removeFriendOrCancelRequest(userId, targetUserId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/block/{targetUserId}")
    fun blockUser(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable targetUserId: UUID
    ): ResponseEntity<Void> {
        service.blockUser(userId, targetUserId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @DeleteMapping("/block/{targetUserId}")
    fun unblockUser(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable targetUserId: UUID
    ): ResponseEntity<Void> {
        service.unblockUser(userId, targetUserId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping
    fun getFriends(
        @AuthenticationPrincipal userId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<FriendshipResponse>> {
        val slice = service.getFriends(userId, pageable)
        return ResponseEntity.ok(slice.map { mapper.toResponse(userId, it) })
    }

    @GetMapping("/requests/incoming")
    fun getIncomingRequests(
        @AuthenticationPrincipal userId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<FriendshipResponse>> {
        val slice = service.getIncomingRequests(userId, pageable)
        return ResponseEntity.ok(slice.map { mapper.toResponse(userId, it) })
    }

    @GetMapping("/requests/incoming/count")
    fun getIncomingRequestsCount(
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<FriendCountResponse> {
        val count = service.getIncomingRequestsCount(userId)
        return ResponseEntity.ok(FriendCountResponse(count))
    }
}