package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.entity.Message
import com.example.project.campusloop.repository.MessageRepository
import com.example.project.campusloop.service.MessageQueryService
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MessageQueryServiceImpl(
    private val repository: MessageRepository
) : MessageQueryService {
    override fun getUnreadCountsByChannelIds(
        userId: UUID,
        channelIds: Collection<Long>
    ): Map<Long, Int> {
        if (channelIds.isEmpty()) return emptyMap()

        return repository.countUnreadByChannelIds(userId, channelIds)
            .associate { it.channelId to it.unreadCount }
    }

    override fun getLatestMessage(channelId: Long): Message? {
        return repository.findFirstByChannelIdOrderByIdDesc(channelId)
    }


}