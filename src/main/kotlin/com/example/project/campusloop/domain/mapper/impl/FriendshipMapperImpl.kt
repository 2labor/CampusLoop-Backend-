package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.dto.FriendshipResponse
import com.example.project.campusloop.domain.entity.Friendship
import com.example.project.campusloop.domain.mapper.FriendshipMapper
import com.example.project.campusloop.domain.mapper.UserProfileMapper
import com.example.project.campusloop.service.UserProfileService
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class FriendshipMapperImpl (
    private val profileService : UserProfileService,
    private val profileMapper : UserProfileMapper
) : FriendshipMapper {

    override fun toResponse(
        currentUserId: UUID,
        friendship: Friendship
    ): FriendshipResponse {
        val otherUser = if (friendship.initializer.id == currentUserId) friendship.target else friendship.initializer

        val profile = profileService.getProfileByUserId(otherUser.id!!)

        return FriendshipResponse(
            id = friendship.id!!,
            user = profileMapper.toResponse(profile),
            status = friendship.status,
            createdAt = friendship.createdAt,
            updatedAt = friendship.updatedAt
        )
    }
}