package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.entity.ChannelMember
import com.example.project.campusloop.domain.entity.User
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID


interface ChannelMemberRepository : JpaRepository<ChannelMember, Long> {
    fun findAllByUser(user: User, pageable: Pageable): Slice<ChannelMember>
    fun findByChannelAndUser(channel: Channel, user: User): ChannelMember?
    fun existsByChannelAndUser(channel: Channel, user: User): Boolean
    fun findAllByChannel(channel: Channel, pageable: Pageable): Slice<ChannelMember>

    @Query("""
        SELECT cm FROM ChannelMember cm
        JOIN FETCH cm.user 
        WHERE cm.channel.id IN :channelIds
    """)
    fun findAllWithUserByChannelIds(
        @Param("channelIds") channelIds: Collection<Long>
    ): List<ChannelMember>

    @Modifying(clearAutomatically = true)
    @Query(
        """
    UPDATE ChannelMember cm
    SET cm.lastReadMessageId = :messageId,
        cm.updatedAt = CURRENT_TIMESTAMP
    WHERE cm.channel.id = :channelId
      AND cm.user.id = :userId
      AND (cm.lastReadMessageId IS NULL OR cm.lastReadMessageId < :messageId)
    """
    )
    fun updateLastReadMessage(
        @Param("channelId") channelId: Long,
        @Param("userId") userId: UUID,
        @Param("messageId") messageId: Long
    ): Int
}