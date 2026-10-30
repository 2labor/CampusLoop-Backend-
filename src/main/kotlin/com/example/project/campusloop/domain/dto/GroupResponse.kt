package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.entity.Group
import java.time.Instant
import java.util.UUID

data class GroupResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val avatarUrl: String?,
    val isPrivate: Boolean,
    val isVerified: Boolean,
    val creatorId: UUID,
    val memberCount: Long,
    val createdAt: Instant
)

fun Group.toResponse(memberCount: Long): GroupResponse {
    return GroupResponse(
        id = id!!,
        name = name,
        description = description,
        avatarUrl = avatarUrl,
        isPrivate = isPrivate,
        isVerified = isVerified,
        creatorId = creator.id!!,
        memberCount = memberCount,
        createdAt = createdAt
    )
}