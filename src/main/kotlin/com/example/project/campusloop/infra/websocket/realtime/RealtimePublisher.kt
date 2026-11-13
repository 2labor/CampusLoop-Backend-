package com.example.project.campusloop.infra.websocket.realtime

import com.example.project.campusloop.infra.websocket.RedisWebSocketBroadcaster
import com.example.project.campusloop.infra.websocket.realtime.enum.RealtimeAction
import com.example.project.campusloop.infra.websocket.realtime.message.RealtimeMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.support.TransactionSynchronization
import org.springframework.transaction.support.TransactionSynchronizationManager
import java.util.UUID

@Service
class RealtimePublisher(
    private val broadcaster: RedisWebSocketBroadcaster
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun <T> publish(destination: String, action: RealtimeAction, entityId: Any, payload: T?) {
        val message = RealtimeMessage(action, entityId, payload)
        afterCommit { broadcaster.broadcast(destination, message) }
    }

    fun <T> sendToUser(userId: UUID, destination: String, action: RealtimeAction, entityId: Any, payload: T?) {
        val message = RealtimeMessage(action, entityId, payload)
        afterCommit { broadcaster.sendToUser(userId, destination, message) }
    }

    private fun afterCommit(block: () -> Unit) {
        val safe = {
            try { block() } catch (e: Exception) { log.error("Realtime publish failed", e) }
        }
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(object : TransactionSynchronization {
                override fun afterCommit() = safe()
            })
        } else {
            safe()
        }
    }
}