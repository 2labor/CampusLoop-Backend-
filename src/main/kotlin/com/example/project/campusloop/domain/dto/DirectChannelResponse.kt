package com.example.project.campusloop.domain.dto

import java.time.Instant
import java.util.UUID

data class DirectChannelResponse(
    val id: Long,
    val userId: UUID,
    val name: String,
    val lastMessage: String?,
    val isMine: Boolean,
    val lastActivityAt: Instant,
    val unreadCount: Int
)
