package com.example.project.campusloop.repository

import com.example.project.campusloop.domain.entity.Attachment
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface AttachmentRepository : JpaRepository<Attachment, UUID> {
    fun findAllByIdIn(ids: List<UUID>): List<Attachment>
    fun findByPostId(postId: UUID, pageable: Pageable): Slice<Attachment>
    fun findByMessageId(messageId: Long, pageable: Pageable): Slice<Attachment>
}