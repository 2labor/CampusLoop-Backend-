package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.ChannelResponse
import com.example.project.campusloop.domain.request.CreateChannelRequest
import com.example.project.campusloop.domain.request.EditChannelRequest
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
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/v1/channels")
class ChannelController (
    private val service: ChannelService
) {
    @PostMapping
    fun createChannel(
        @AuthenticationPrincipal userId: UUID,
        @RequestBody request: CreateChannelRequest
    ): ResponseEntity<ChannelResponse> {
        val channel = service.createChannel(
            userId,
            request.groupId,
            request.name,
            request.type
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(ChannelResponse.from(channel))
    }

    @PutMapping("/{channelId}")
    fun editChannel(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable channelId: Long,
        @RequestBody request: EditChannelRequest
    ): ResponseEntity<ChannelResponse> {
        val channel = service.editChannel(
            userId,
            channelId,
            request.name
        )

        return ResponseEntity.ok(ChannelResponse.from(channel))
    }

    @DeleteMapping("/{channelId}")
    fun deleteChannel(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable channelId: Long
    ): ResponseEntity<Void> {
        service.deleteChannel(userId, channelId)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/group/{groupId}")
    fun getGroupChannels(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable groupId: UUID,
        pageable: Pageable
    ): ResponseEntity<Slice<ChannelResponse>> {
        val channels = service.getGroupChannels(userId, groupId, pageable)
        return ResponseEntity.ok(channels.map { ChannelResponse.from(it) })
    }

    @GetMapping("/{channelId}")
    fun getChannelById(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable channelId: Long
    ): ResponseEntity<ChannelResponse> {
        val channel = service.getChannelById(userId, channelId)
        return ResponseEntity.ok(ChannelResponse.from(channel))
    }
}