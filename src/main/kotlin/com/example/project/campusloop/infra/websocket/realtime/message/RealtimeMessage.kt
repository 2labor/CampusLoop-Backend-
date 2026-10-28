package com.example.project.campusloop.infra.websocket.realtime.message

import com.example.project.campusloop.infra.websocket.realtime.enum.RealtimeAction
import java.util.UUID

data class RealtimeMessage<T>(
    val action: RealtimeAction,
    val entityId: UUID,
    val payload: T?
)
