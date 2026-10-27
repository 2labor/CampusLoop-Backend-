package com.example.project.campusloop.infra.websocket

import com.example.project.campusloop.security.JwtCore
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component

@Component
class JwtChannelInterceptor (
    private val jwtCore: JwtCore,
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)

        if (accessor != null && StompCommand.CONNECT == accessor.command) {
            val authHeader = accessor.getFirstNativeHeader("Authorization")

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                val token = authHeader.substring(7)

                if (jwtCore.validToken(token)) {
                    val userId = jwtCore.getUserIdFromToken(token)
                    accessor.user = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                } else {
                    throw IllegalArgumentException("Token invalid")
                }
            } else {
                throw IllegalArgumentException("Missing Authorization header in WebSocket handshake")
            }
        }

        return message
    }

}