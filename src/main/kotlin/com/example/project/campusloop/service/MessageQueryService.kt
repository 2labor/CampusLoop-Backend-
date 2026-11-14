package com.example.project.campusloop.service

import com.example.project.campusloop.domain.entity.Message
import java.util.UUID

interface MessageQueryService {
    fun getUnreadCountsByChannelIds(userId: UUID, channelIds: Collection<Long>): Map<Long, Int>
    fun getLatestMessage(channelId: Long): Message?
}