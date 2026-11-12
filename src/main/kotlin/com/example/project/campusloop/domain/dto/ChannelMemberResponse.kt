package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.entity.ChannelMember
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant
import java.util.UUID

data class ChannelMemberResponse(
    @field:JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val userId: UUID,
    val username: String,
    @field:JsonSerialize(using = ToStringSerializer::class)
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