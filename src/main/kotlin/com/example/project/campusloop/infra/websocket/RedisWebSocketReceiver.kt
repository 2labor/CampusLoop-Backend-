package com.example.project.campusloop.infra.websocket
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class RedisWebSocketReceiver (
    private val messagingTemplate: SimpMessagingTemplate
){
    fun handleBroadcast(message: Any, channel: String) {
        val stompDestination = channel.removePrefix("ws:broadcast")
        messagingTemplate.convertAndSend(stompDestination, message)
    }

    fun handleUserMessage(message: Any, channel: String) {
        val parts = channel.split(":")
        if (parts.size >= 4) {
            val userId = parts[2]
            val stompDestination = parts.drop(3).joinToString(":")
            messagingTemplate.convertAndSendToUser(userId, stompDestination, message)
        }
    }

}