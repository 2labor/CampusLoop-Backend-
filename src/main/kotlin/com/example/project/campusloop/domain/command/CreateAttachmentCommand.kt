package com.example.project.campusloop.domain.command

import com.example.project.campusloop.domain.enum.FileType

data class CreateAttachmentCommand(
    val fileSize: Long,
    val originalName: String,
    val fileType: FileType,
    val fileKey: String,
    val mimeType: String
)
