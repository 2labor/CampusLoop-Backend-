package com.example.project.campusloop.service

import com.example.project.campusloop.domain.dto.DirectChannelSummary
import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.enum.ChannelType
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.time.Instant
import java.util.UUID

interface ChannelService {
    fun createChannel(operatorId: UUID, groupId: UUID? = null, name: String? = null, type: ChannelType): Channel
    fun editChannel(operatorId: UUID, channelId: Long, name: String): Channel
    fun deleteChannel(operatorId: UUID, channelId: Long)

    fun getGroupChannels(operatorId: UUID, groupId: UUID, pageable: Pageable): Slice<Channel>
    fun getChannelById(operatorId: UUID, channelId: Long): Channel
    fun getUserDirectChannels(userId: UUID, pageable: Pageable ): Slice<DirectChannelSummary>

    fun onNewMessage(channelId: Long, messageId: Long, senderId: UUID, rawContent: String?, createdAt: Instant)
    fun onMessageDelete(channelId: Long, deletedMessageId: Long)
}