package com.example.project.campusloop.infra.websocket

import com.example.project.campusloop.security.JwtCore
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class JwtChannelInterceptor (
    private val jwtCore: JwtCore,
) : ChannelInterceptor {

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
            ?: return message

        when (accessor.command) {
            StompCommand.CONNECT -> {
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

            StompCommand.SUBSCRIBE -> {
                val principal = accessor.user
                    ?: throw AccessDeniedException("Unauthenticated WebSocket subscription rejected")
                val callerId = (principal as? UsernamePasswordAuthenticationToken)?.principal as? UUID
                    ?: UUID.fromString(principal.name)

                val destination = accessor.destination
                    ?: throw IllegalArgumentException("Missing subscription destination")

                authorizeSubscription(callerId, destination)
            }

            else -> {}
        }

        return message
    }

    private fun authorizeSubscription(callerId: UUID, destination: String) {
        val userTopicPrefix = "/topic/users/"
        if (destination.startsWith(userTopicPrefix)) {
            val remainder = destination.removePrefix(userTopicPrefix)
            val targetUserSegment = remainder.substringBefore("/")
            if (targetUserSegment != callerId.toString()) {
                throw AccessDeniedException("Cannot subscribe to another user's private topic")
            }
        }

        val userQueuePrefix = "/user/"
        if (destination.startsWith(userQueuePrefix) && !destination.startsWith("/user/queue/")) {
            val remainder = destination.removePrefix(userQueuePrefix)
            val targetUserSegment = remainder.substringBefore("/")
            if (targetUserSegment != callerId.toString()) {
                throw AccessDeniedException("Cannot subscribe to another user's private queue")
            }
        }
    }

}
