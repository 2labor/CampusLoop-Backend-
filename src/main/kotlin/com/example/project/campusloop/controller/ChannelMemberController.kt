package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.ChannelMemberResponse
import com.example.project.campusloop.domain.dto.ChannelResponse
import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.entity.ChannelMember
import com.example.project.campusloop.service.ChannelMemberService
import com.example.project.campusloop.service.ChannelService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/channels")
class ChannelMemberController (
    private val service: ChannelMemberService
) {
    @PostMapping("/{channelId}/members")
    fun addMember(
        @AuthenticationPrincipal operatorId: UUID,
        @PathVariable channelId: Long,
        @RequestParam userId: UUID
    ): ResponseEntity<ChannelMemberResponse> {
        val member = service.addMember(operatorId, channelId, userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(ChannelMemberResponse.from(member))
    }

    @DeleteMapping("/{channelId}/members/{memberId}")
    fun removeMember(
        @AuthenticationPrincipal operatorId: UUID,
        @PathVariable channelId: Long,
        @PathVariable memberId: Long
    ): ResponseEntity<Void> {
        service.removeMember(operatorId, channelId, memberId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{channelId}/members")
    fun getAllMembers(
        @AuthenticationPrincipal operatorId: UUID,
        @PathVariable channelId: Long,
        pageable: Pageable
    ): ResponseEntity<Slice<ChannelMemberResponse>> {
        val members = service.getAllMembers(operatorId, channelId, pageable)
        return ResponseEntity.ok(members.map { ChannelMemberResponse.from(it) })
    }

    @GetMapping("/me")
    fun getMyChannels(
        @AuthenticationPrincipal operatorId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<ChannelResponse>> {
        val channelSlice = service.getUsersChannels(operatorId, pageable)
        return ResponseEntity.ok(channelSlice.map { ChannelResponse.from(it) })
    }

    @PostMapping("/direct")
    fun getOrCreateDirectChannel(
        @AuthenticationPrincipal operatorId: UUID,
        @RequestParam userId: UUID
    ): ResponseEntity<ChannelResponse> {
        val channel = service.getOrCreateDirectChannel(operatorId, userId)
        return ResponseEntity.ok(ChannelResponse.from(channel))
    }
}