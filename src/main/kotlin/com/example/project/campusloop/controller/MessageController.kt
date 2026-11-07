package com.example.project.campusloop.controller

import com.example.project.campusloop.domain.dto.LastMessageResponse
import com.example.project.campusloop.domain.dto.MessageResponse
import com.example.project.campusloop.domain.enum.HistoryDirection
import com.example.project.campusloop.domain.mapper.MessageMapper
import com.example.project.campusloop.domain.request.CreateMessageRequest
import com.example.project.campusloop.domain.request.EditMessageRequest
import com.example.project.campusloop.service.MessageService
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
@RequestMapping("/api/v1/channels/{channelId}/messages")
class MessageController (
    private val service: MessageService,
    private val mapper: MessageMapper
) {
    @PostMapping
    fun sendMessage(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable channelId: Long,
        @RequestBody request: CreateMessageRequest
    ): ResponseEntity<MessageResponse> {
        val command = mapper.toCommand(request)
        val message = service.sendMessage(
            userId,
            channelId,
            command
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(message))
    }

    @PutMapping("/{messageId}")
    fun editMessage(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable messageId: Long,
        @RequestBody request: EditMessageRequest
    ): ResponseEntity<MessageResponse> {
        val command = mapper.toCommand(request)
        val message = service.editMessage(
            userId,
            messageId,
            command,
        )

        return ResponseEntity.ok(mapper.toResponse(message))
    }

    @DeleteMapping("/{messageId}")
    fun deleteMessage(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable messageId: Long
    ): ResponseEntity<Void> {
        service.deleteMessage(
            userId,
            messageId
        )

        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{messageId}")
    fun getMessageById(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable messageId: Long
    ): ResponseEntity<MessageResponse> {
        val message = service.getMessageById(
            userId,
            messageId
        )

        return ResponseEntity.ok(mapper.toResponse(message))
    }

    @GetMapping("/last")
    fun getLastMessage(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable channelId: Long
    ): ResponseEntity<LastMessageResponse> {
        val message = service.getLastMessage(userId, channelId)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(mapper.toLastMessageResponse(message))
    }

    @GetMapping
    fun getChannelHistory(
        @AuthenticationPrincipal userId: UUID,
        @PathVariable channelId: Long,
        @RequestParam(required = false) cursorId: Long?,
        @RequestParam(defaultValue = "BEFORE") direction: HistoryDirection,
        pageable: Pageable
    ): ResponseEntity<Slice<LastMessageResponse>> {
        val messages = service.getChannelHistory(
            operatorId = userId,
            channelId = channelId,
            cursorId = cursorId,
            direction = direction,
            pageable = pageable
        )
        return ResponseEntity.ok(messages.map { mapper.toLastMessageResponse(it) })
    }
}