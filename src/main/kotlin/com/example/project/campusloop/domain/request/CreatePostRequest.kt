package com.example.project.campusloop.domain.request

import com.example.project.campusloop.domain.command.CreateAttachmentCommand
import com.example.project.campusloop.domain.dto.AttachmentResponse
import java.util.UUID

data class CreatePostRequest(
    val title: String?,
    val content: String,
    val isAnonymous: Boolean = false,
    val attachments: List<CreateAttachmentCommand> = emptyList(),
)
