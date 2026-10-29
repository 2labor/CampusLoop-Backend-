package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.FriendshipStatus
import java.time.Instant
import java.util.UUID

data class FriendshipResponse(
    val id: UUID,
    val user: UserProfileResponse,
    val status: FriendshipStatus,
    val createdAt: Instant,
    val updatedAt: Instant
)
