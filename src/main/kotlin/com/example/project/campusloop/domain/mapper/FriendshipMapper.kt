package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.dto.FriendshipResponse
import com.example.project.campusloop.domain.entity.Friendship
import java.util.UUID

interface FriendshipMapper {
    fun toResponse(currentUserId: UUID, friendship: Friendship): FriendshipResponse
}