package com.example.project.campusloop.infra.websocket.realtime.payload

import com.example.project.campusloop.domain.enum.ParticipantStatus
import java.util.UUID

data class ParticipantRealtimePayload(
    val userId: UUID,
    val username: String,
    val status: ParticipantStatus
)
