package com.example.project.campusloop.infra.websocket

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RedisWebSocketBroadcaster (
    private val redisTemplate: RedisTemplate<String, Any>,
) {

    fun broadcast(destination: String, payload: Any) {
        redisTemplate.convertAndSend("ws:broadcast:$destination", payload)
    }

    fun sendToUser(userId: UUID, destination: String, payload: Any) {
        redisTemplate.convertAndSend("ws:sendToUser:$userId:$destination", payload)
    }
}