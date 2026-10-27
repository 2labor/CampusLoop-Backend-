package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.ParticipantStatus
import java.time.LocalDateTime
import java.util.UUID

data class ParticipantResponse(
    val id: UUID,
    val userId: UUID,
    val username: String,
    val status: ParticipantStatus,
    val joinedAt: LocalDateTime
)
