package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.entity.ChannelMember
import java.time.Instant
import java.util.UUID

data class ChannelMemberResponse(
    val id: Long,
    val userId: UUID,
    val username: String,
    val channelId: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun from(member: ChannelMember): ChannelMemberResponse {
            return ChannelMemberResponse(
                id = member.id!!,
                userId = member.user.id!!,
                username = member.user.username,
                channelId = member.channel.id!!,
                createdAt = member.createdAt,
                updatedAt = member.updatedAt,
            )
        }
    }
}