package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.entity.Group
import com.example.project.campusloop.domain.enum.ChannelType
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.Instant
import java.util.UUID

interface ChannelRepository : JpaRepository<Channel, Long> {
    fun findAllByGroup(group: Group, pageable: Pageable): Slice<Channel>
    @Query("""
        SELECT c FROM Channel c 
        JOIN ChannelMember m1 ON m1.channel = c 
        JOIN ChannelMember m2 ON m2.channel = c 
        WHERE c.type = 'DIRECT' AND m1.user.id = :userId1 AND m2.user.id = :userId2
    """)
    fun findDirectChannelBetweenUsers(@Param("userId1") userId1: UUID, @Param("userId2") userId2: UUID): Channel?
    @Query(
        """
        SELECT c FROM Channel c
        JOIN ChannelMember cm ON cm.channel = c
        WHERE cm.user.id = :userId
          AND c.type = :type
        ORDER BY c.updatedAt DESC
        """
    )
    fun findActiveDirectMessages(
        @Param("userId") userId: UUID,
        @Param("type") type: ChannelType = ChannelType.DIRECT,
        pageable: Pageable
    ): Slice<Channel>

    @Modifying(clearAutomatically = true)
    @Query(
        """
    UPDATE Channel c
    SET c.lastMessageId = :messageId,
        c.lastMessageContent = :contentPreview,
        c.lastSenderId = :senderId,
        c.lastActivityAt = :activityAt,
        c.updatedAt = :activityAt
    WHERE c.id = :channelId
      AND (c.lastMessageId IS NULL OR c.lastMessageId < :messageId)
    """
    )
    fun updateLastMessageMetadata(
        @Param("channelId") channelId: Long,
        @Param("messageId") messageId: Long,
        @Param("contentPreview") contentPreview: String?,
        @Param("senderId") senderId: UUID,
        @Param("activityAt") activityAt: Instant
    ): Int


}