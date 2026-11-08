package com.example.project.campusloop.infra.websocket
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class RedisWebSocketReceiver (
    private val messagingTemplate: SimpMessagingTemplate
){
    fun handleBroadcast(message: Any, channel: String) {
        val stompDestination = channel.removePrefix("ws:broadcast:")
        messagingTemplate.convertAndSend(stompDestination, message)
    }

    fun handleUserMessage(payload: Any, pattern: String) {
        val prefix = "ws:user:"
        if (pattern.startsWith(prefix)) {
            val remaining = pattern.substring(prefix.length)
            val firstColonIndex = remaining.indexOf(':')
            if (firstColonIndex != -1) {
                val userId = remaining.substring(0, firstColonIndex)
                val stompDestination = remaining.substring(firstColonIndex + 1)
                messagingTemplate.convertAndSendToUser(userId, stompDestination, payload)
            }
        }
    }

}