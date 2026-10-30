package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.entity.GroupMember
import com.example.project.campusloop.domain.enum.GroupMemberRole
import com.example.project.campusloop.domain.enum.GroupMemberStatus
import java.time.Instant
import java.util.UUID

data class GroupMemberResponse(
    val id: UUID,
    val userId: UUID,
    val username: String,
    val role: GroupMemberRole,
    val status: GroupMemberStatus,
    val joinedAt: Instant
)

fun GroupMember.toResponse(): GroupMemberResponse {
    return GroupMemberResponse(
        id = id!!,
        userId = user.id!!,
        username = user.username,
        role = role,
        status = status,
        joinedAt = createdAt
    )
}
