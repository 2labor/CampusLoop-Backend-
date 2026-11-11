package com.example.project.campusloop.domain.dto

import com.example.project.campusloop.domain.enum.FileType
import java.time.Instant
import java.util.UUID

data class AttachmentResponse (
    val id: UUID,
    val fileSize: Long,
    val originalName: String,
    val mediaUrl: String,
    val mimeType: String,
    val type: FileType,
    val createdAt: Instant
)