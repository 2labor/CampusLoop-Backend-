package com.example.project.campusloop.infra.websocket

import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Component

@Component
class RedisWebSocketReceiver(
    private val messagingTemplate: SimpMessagingTemplate,
    private val serializer: GenericJackson2JsonRedisSerializer
) : MessageListener {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun onMessage(message: Message, pattern: ByteArray?) {
        try {
            val channel = String(message.channel, Charsets.UTF_8)
            val payload = serializer.deserialize(message.body) ?: return
            log.debug("Redis -> WS: channel={}", channel)

            when {
                channel.startsWith(BROADCAST) ->
                    messagingTemplate.convertAndSend(channel.removePrefix(BROADCAST), payload)

                channel.startsWith(USER) -> {
                    val rest = channel.removePrefix(USER)
                    val idx = rest.indexOf(':')
                    if (idx != -1) {
                        messagingTemplate.convertAndSendToUser(
                            rest.substring(0, idx),
                            rest.substring(idx + 1),
                            payload
                        )
                    }
                }
            }
        } catch (e: Exception) {
            log.error("Failed to process redis message", e)
        }
    }

    private companion object {
        const val BROADCAST = "ws:broadcast:"
        const val USER = "ws:user:"
    }
}