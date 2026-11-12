package com.example.project.campusloop.domain.dto

import java.time.Instant

data class MessageResponse(
    val id: Long,
    val content: String,
    val senderName: String,
    val channelId: Long,
    val attachments: List<AttachmentResponse> = emptyList(),
    val parentContent: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)