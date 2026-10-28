package com.example.project.campusloop.infra.websocket.realtime.service

import com.example.project.campusloop.domain.entity.Event
import com.example.project.campusloop.domain.entity.EventParticipant
import com.example.project.campusloop.domain.enum.NotificationType
import com.example.project.campusloop.domain.enum.ParticipantStatus
import com.example.project.campusloop.infra.websocket.realtime.RealtimePublisher
import com.example.project.campusloop.infra.websocket.realtime.enum.RealtimeAction
import com.example.project.campusloop.infra.websocket.realtime.payload.ParticipantRealtimePayload
import com.example.project.campusloop.infra.websocket.realtime.payload.toPayload
import com.example.project.campusloop.service.NotificationService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class EventDispatchService (
    private val realtimePublisher: RealtimePublisher,
    private val notificationService: NotificationService
) {
    fun notifyEventCreated(event: Event) {
        realtimePublisher.publish(
            destination = "/topic/events",
            action = RealtimeAction.CREATED,
            entityId = event.id!!,
            payload = event.toPayload()
        )
    }

    fun notifyEventUpdated(event: Event) {
        realtimePublisher.publish(
            destination = "/topic/events",
            action = RealtimeAction.UPDATED,
            entityId = event.id!!,
            payload = event.toPayload()
        )
    }

    fun notifyParticipantJoined(eventId: UUID, participant: EventParticipant) {
        realtimePublisher.publish(
            destination = "/topic/events/$eventId/participants",
            action = RealtimeAction.CREATED,
            entityId = participant.user.id!!,
            payload = ParticipantRealtimePayload(
                userId = participant.user.id!!,
                username = participant.user.username,
                status = participant.status
            )
        )

        if (participant.status == ParticipantStatus.PENDING) {
            notificationService.createAndSend(
                receiverId = participant.event.creator.id!!,
                title = "New application",
                message = "User ${participant.user.username} would like to join to '${participant.event.title}'",
                type = NotificationType.EVENT_INVITATION
            )
        }

        else if (participant.status == ParticipantStatus.APPROVED) {
            notificationService.createAndSend(
                receiverId = participant.event.creator.id!!,
                title = "New participant",
                message = "User ${participant.user.username} join to '${participant.event.title}' event!",
                type = NotificationType.EVENT_PARTICIPANT_JOIN
            )
        }
    }

    fun notifyParticipantLeft(eventId: UUID, userId: UUID) {
        realtimePublisher.publish(
            destination = "/topic/events/$eventId/participants",
            action = RealtimeAction.DELETED,
            entityId = userId,
            payload = null
        )
    }

    fun notifyParticipantStatusUpdated(eventId: UUID, participant: EventParticipant) {
        realtimePublisher.publish(
            destination = "/topic/events/$eventId/participants",
            action = RealtimeAction.UPDATED,
            entityId = participant.user.id!!,
            payload = ParticipantRealtimePayload(
                userId = participant.user.id!!,
                username = participant.user.username,
                status = participant.status
            )
        )

        if (participant.status == ParticipantStatus.APPROVED) {
            notificationService.createAndSend(
                receiverId = participant.user.id!!,
                title = "Application Approved! 🎉",
                message = "Your participation in the event '${participant.event.title}' has been confirmed.",
                type = NotificationType.EVENT_PARTICIPANT_APPROVED
            )
        }
    }
}