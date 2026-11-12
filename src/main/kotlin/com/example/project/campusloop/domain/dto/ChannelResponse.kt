package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.enum.ChannelType
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import java.time.Instant
import java.util.UUID

data class ChannelResponse(
    @field:JsonSerialize(using = ToStringSerializer::class)
    val id: Long,
    val name: String?,
    val type: ChannelType,
    val groupId: UUID?,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    companion object {
        fun from(channel: Channel): ChannelResponse {
            return ChannelResponse(
                id = channel.id!!,
                name = channel.name,
                type = channel.type,
                groupId = channel.group?.id,
                createdAt = channel.createdAt,
                updatedAt = channel.updatedAt
            )
        }
    }
}