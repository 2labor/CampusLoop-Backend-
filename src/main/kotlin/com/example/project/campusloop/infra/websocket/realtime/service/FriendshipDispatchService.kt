package com.example.project.campusloop.infra.websocket.realtime.service

import com.example.project.campusloop.domain.entity.Friendship
import com.example.project.campusloop.domain.enum.NotificationType
import com.example.project.campusloop.infra.websocket.realtime.RealtimePublisher
import com.example.project.campusloop.infra.websocket.realtime.enum.RealtimeAction
import com.example.project.campusloop.infra.websocket.realtime.payload.toRealtimePayload
import com.example.project.campusloop.service.NotificationService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class FriendshipDispatchService(
    private val realtimePublisher: RealtimePublisher,
    private val notificationService: NotificationService
) {

    fun notifyFriendRequestSent(friendship: Friendship) {
        realtimePublisher.publish(
            destination = "/topic/users/${friendship.target.id}/friends",
            action = RealtimeAction.CREATED,
            entityId = friendship.id!!,
            payload = friendship.toRealtimePayload()
        )

        notificationService.createAndSend(
            receiverId = friendship.target.id!!,
            title = "New Friend Request 🤝",
            message = "User ${friendship.initializer.username} sent you a friend request.",
            type = NotificationType.FRIEND_REQUEST
        )
    }

    fun notifyFriendRequestAccepted(friendship: Friendship) {
        realtimePublisher.publish(
            destination = "/topic/users/${friendship.initializer.id}/friends",
            action = RealtimeAction.UPDATED,
            entityId = friendship.id!!,
            payload = friendship.toRealtimePayload()
        )

        notificationService.createAndSend(
            receiverId = friendship.initializer.id!!,
            title = "Friend Request Accepted 🎉",
            message = "User ${friendship.target.username} accepted your friend request!",
            type = NotificationType.FRIEND_REQUEST_ACCEPTED
        )
    }

    fun notifyFriendshipRemoved(friendship: Friendship, targetUserId: UUID) {
        realtimePublisher.publish(
            destination = "/topic/users/$targetUserId/friends",
            action = RealtimeAction.DELETED,
            entityId = friendship.id!!,
            payload = null
        )
    }

    fun notifyUserBlocked(friendship: Friendship) {
        realtimePublisher.publish(
            destination = "/topic/users/${friendship.target.id}/friends",
            action = RealtimeAction.DELETED,
            entityId = friendship.id!!,
            payload = null
        )
    }

}