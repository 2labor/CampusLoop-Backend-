package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Channel
import com.example.project.campusloop.domain.entity.Group
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
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
}