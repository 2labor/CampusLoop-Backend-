package com.example.project.campusloop.infra.websocket.realtime

import com.example.project.campusloop.infra.websocket.RedisWebSocketBroadcaster
import com.example.project.campusloop.infra.websocket.realtime.enum.RealtimeAction
import com.example.project.campusloop.infra.websocket.realtime.message.RealtimeMessage
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class RealtimePublisher (
    private val broadcaster: RedisWebSocketBroadcaster
){
    fun <T> publish(
        destination: String,
        action: RealtimeAction,
        entityId: UUID,
        payload: T?
    ){
        val message = RealtimeMessage(
            action = action,
            entityId = entityId,
            payload = payload
        )

        broadcaster.broadcast(destination, message)
    }
}