package com.example.project.campusloop.domain.mapper.impl

import com.example.project.campusloop.domain.dto.AttachmentResponse
import com.example.project.campusloop.domain.entity.Attachment
import com.example.project.campusloop.domain.mapper.AttachmentMapper
import com.example.project.campusloop.infra.storage.service.StorageService
import org.springframework.stereotype.Component

@Component
class AttachmentMapperImpl(
    private val storageService: StorageService
) : AttachmentMapper  {
    override fun toResponseList(attachments: List<Attachment>): List<AttachmentResponse> {
        return attachments.map { toResponse(it) }
    }

    private fun toResponse(attachment: Attachment): AttachmentResponse {
        val resolveUrl = resolveMediaUrl(attachment.fileKey)

        return AttachmentResponse(
            id = attachment.id!!,
            fileSize = attachment.fileSize,
            originalName = attachment.originalName,
            mediaUrl = resolveUrl,
            mimeType = attachment.mimeType,
            type = attachment.fileType,
            createdAt = attachment.createdAt,
        )
    }

    private fun resolveMediaUrl(fileKey: String): String {
        return if (fileKey.startsWith("http://") || fileKey.startsWith("https://")) {
            fileKey
        } else {
            storageService.getFileUrl(fileKey)
        }
    }
}