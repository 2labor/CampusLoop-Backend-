package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Message
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository

interface MessageRepository : JpaRepository<Message, Long> {
    fun findFirstByChannelIdOrderByIdDesc(channelId: Long): Message?

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
}