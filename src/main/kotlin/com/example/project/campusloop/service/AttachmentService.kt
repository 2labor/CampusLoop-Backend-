package com.example.project.campusloop.service

import com.example.project.campusloop.domain.command.CreateAttachmentCommand
import com.example.project.campusloop.domain.entity.Attachment
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import java.util.UUID

interface AttachmentService {
    fun createAttachment(command: CreateAttachmentCommand): Attachment
    fun getAttachmentById(attachmentId: UUID): Attachment
    fun getAttachmentByIds(ids: List<UUID>): List<Attachment>
    fun getPostAttachments(postId: UUID, pageable: Pageable): Slice<Attachment>
    fun getMessageAttachments(messageId: Long, pageable: Pageable): Slice<Attachment>
    fun deleteAttachment(attachment: Attachment)
    fun deleteAllAttachments(attachments: List<Attachment>)
}