package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Message
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface MessageRepository : JpaRepository<Message, Long> {
    fun findFirstByChannelIdOrderByIdDesc(channelId: Long): Message?

    fun existsByIdAndChannel_Id(id: Long, channelId: Long): Boolean

    fun findAllByChannelIdOrderByIdDesc(channelId: Long, pageable: Pageable): Slice<Message>

    fun findAllByChannelIdAndIdLessThanOrderByIdDesc(
        channelId: Long,
        cursorId: Long,
        pageable: Pageable
    ): Slice<Message>

    fun findAllByChannelIdAndIdGreaterThanOrderByIdAsc(
        channelId: Long,
        cursorId: Long,
        pageable: Pageable
    ): Slice<Message>

    @Query(
        """
        SELECT m.channel.id AS channelId, COUNT(m.id) AS unreadCount
        FROM Message m
        JOIN ChannelMember cm ON cm.channel.id = m.channel.id
        WHERE cm.user.id = :userId
          AND m.channel.id IN :channelIds
          AND m.id > COALESCE(cm.lastReadMessageId, 0)
        GROUP BY m.channel.id
        """
    )
    fun countUnreadByChannelIds(
        @Param("userId") userId: UUID,
        @Param("channelIds") channelIds: Collection<Long>
    ): List<ChannelUnreadProjection>
}

interface ChannelUnreadProjection {
    val channelId: Long
    val unreadCount: Int
}