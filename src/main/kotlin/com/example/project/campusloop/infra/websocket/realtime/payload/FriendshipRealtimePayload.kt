package com.example.project.campusloop.infra.websocket.realtime.payload

import com.example.project.campusloop.domain.entity.Friendship
import com.example.project.campusloop.domain.enum.FriendshipStatus
import java.util.UUID

data class FriendshipRealtimePayload(
    val friendshipId: UUID,
    val initializerId: UUID,
    val targetId: UUID,
    val status: FriendshipStatus
)

fun Friendship.toRealtimePayload(): FriendshipRealtimePayload {
    return FriendshipRealtimePayload(
        friendshipId = id!!,
        initializerId = initializer.id!!,
        targetId = target.id!!,
        status = status
    )
}