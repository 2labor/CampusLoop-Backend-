package com.example.project.campusloop.domain.mapper

import com.example.project.campusloop.domain.dto.DirectChannelResponse
import com.example.project.campusloop.domain.dto.DirectChannelSummary
import java.util.UUID

interface ChannelMapper {
    fun toResponse(entity: DirectChannelSummary, currentUserId: UUID): DirectChannelResponse
}