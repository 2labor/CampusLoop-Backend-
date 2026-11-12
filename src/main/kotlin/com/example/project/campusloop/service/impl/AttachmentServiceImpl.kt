package com.example.project.campusloop.service.impl

import com.example.project.campusloop.domain.command.CreateAttachmentCommand
import com.example.project.campusloop.domain.entity.Attachment
import com.example.project.campusloop.infra.storage.service.StorageService
import com.example.project.campusloop.repository.AttachmentRepository
import com.example.project.campusloop.service.AttachmentService
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AttachmentServiceImpl (
    private val repository: AttachmentRepository,
    private val storageService: StorageService,
) : AttachmentService {
    override fun createAttachment(command: CreateAttachmentCommand): Attachment {
        val attachment = Attachment(
            fileSize = command.fileSize,
            fileType = command.fileType,
            fileKey = command.fileKey,
            mimeType = command.mimeType,
            originalName = command.originalName,
        )

        return repository.save(attachment)
    }

    override fun getAttachmentById(attachmentId: UUID): Attachment {
        return repository.findByIdOrNull(attachmentId)
            ?: throw IllegalArgumentException("No attachment with id $attachmentId")
    }

    override fun getAttachmentByIds(ids: List<UUID>): List<Attachment> {
        return repository.findAllByIdIn(ids)
    }

    override fun getPostAttachments(
        postId: UUID,
        pageable: Pageable
    ): Slice<Attachment> {
        return repository.findByPostId(postId, pageable)
    }

    override fun getMessageAttachments(
        messageId: Long,
        pageable: Pageable
    ): Slice<Attachment> {
        return repository.findByMessageId(messageId, pageable)
    }

    override fun deleteAttachment(attachment: Attachment) {
        runCatching {
            storageService.deleteFile(attachment.fileKey)
        }

        repository.delete(attachment)
    }

    override fun deleteAllAttachments(attachments: List<Attachment>) {
        if (attachments.isEmpty()) return
        runCatching {
            attachments.forEach { attachment ->
                storageService.deleteFile(attachment.fileKey)
            }
        }
        repository.deleteAll(attachments)
    }
}